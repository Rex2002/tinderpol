const http = require('https');

async function onRateLimit(waitTime = 5 * 60 * 1000) {
	let min = Math.floor(waitTime / 60000);
	let sec = waitTime - min * 60000;
	console.log('Rate Limited, waiting ' + min + ' minutes and ' + sec + ' seconds...');

	return new Promise((resolve, reject) => {
		setTimeout(() => resolve(), waitTime);
	});
}

async function getJSONReq(url) {
	// Adapted from example in documentation here:
	// https://nodejs.org/api/http.html#httpgeturl-options-callback

	return new Promise(async (resolve, reject) => {
		http.get(url, async (res) => {
			// Error Handling:
			const { statusCode, statusMessage } = res;
			const contentType = res.headers['content-type'];
			let err = null;
			// Check for error in response
			if (statusCode < 200 || statusCode >= 300) {
				console.error('Error requesting URL:', url);
				// If Rate-Limited, then access will be denied for a short period of time
				// exact time is not documented sadly
				if (400 <= statusCode && statusCode < 500) {
					await onRateLimit();
					const data = await getJSONReq(url);
					return resolve(data);
				}
				// Otherwise it's an error that we don't have any handling for
				else {
					err = new Error(`Status Code: ${statusCode}\nMessage: ${statusMessage}`);
				}
			}
			// Check for correct content type
			else if (!/^application\/json/.test(contentType)) {
				err = new Error('Invalid content-type.\n' + `Expected application/json but received ${contentType}`);
			}
			// Handle Error
			if (err) {
				res.resume(); // Consume response data to free up memory
				reject(err);
			}

			// Stream data & returned parsed JSON
			res.setEncoding('utf8');
			let rawData = '';
			res.on('data', (chunk) => {
				rawData += chunk;
			});
			res.on('end', () => {
				try {
					resolve(JSON.parse(rawData));
				} catch (e) {
					console.log(rawData);
					reject(e);
				}
			});
		});
	});
}

async function getCountryCodes() {
	return new Promise((resolve, reject) => {
		try {
			const ccAPIUrl = 'https://restcountries.com/v3.1/all';
			getJSONReq(ccAPIUrl).then((res) => {
				resolve(res.map((x) => x['cca2']));
			});
		} catch (e) {
			reject(e);
		}
	});
}

function getPerc(num, denom, precision = 3) {
	let factor = 10 ** precision;
	return Math.floor((100 * factor * num) / denom) / factor;
}

function normalizeNotice(notice, type, images = null) {
	const charges = [];
	if (Array.isArray(notice?.arrest_warrants)) {
		charges.push(
			...notice.arrest_warrants.map((x) => {
				return {
					country: x?.issuing_country_id ?? '',
					charge: x?.charge ?? '',
				};
			})
		);
	} else if (type === 'un' && notice?.main_activity) {
		charges.push({ country: 'un', charge: notice.main_activity });
	}

	return {
		id: notice?.entity_id ?? '',
		type,
		firstName: notice?.forename ?? '',
		lastName: notice?.name ?? '',
		birthDate: notice?.date_of_birth ?? '',
		nationalities: notice?.nationalities ?? [],
		imgs: images ?? [],
		sex: notice?.sex_id ?? 'U',
		birthCountry: notice?.country_of_birth_id ?? '',
		birthPlace: notice?.place_of_birth ?? '',
		spokenLanguages: notice?.languages_spoken_ids ?? [],
		charges,
		weight: notice?.weight || null,
		height: notice?.height || null,
	};
}

async function getImages(url = null) {
	if (!url || typeof url !== 'string') return [];
	const res = await getJSONReq(url);
	return res?._embedded?.images?.map((x) => x?._links?.self?.href ?? null)?.filter((x) => x !== null) ?? [];
}

// For sorting by ID as numbesr: cb = (str) => str?.split('/')?.map((x) => Number(x)) || [0, 0], cmp = (a, b) => a[0] - b[0] || a[1] - b[1]
// For sorting by ID as strings: cb = (str) => str, cmp = (a, b) => a.localeCompare(b)
function uniqSorted(arr, key = 'entity_id', cb = (str) => str?.split('/')?.map((x) => Number(x)) || [0, 0], cmp = (a, b) => a[0] - b[0] || a[1] - b[1]) {
	arr.sort((a, b) => cmp(cb(a[key]), cb(b[key])));
	return arr.filter((notice, idx, arr) => !idx || notice != arr[idx - 1]);
}

async function getNoticesOfType(type, singleReq) {
	const countryCodes = await getCountryCodes();
	const noticesURL = 'https://ws-public.interpol.int/notices/v1/' + type.toLowerCase();
	let notices = [];

	async function req(...keyArgs) {
		keyArgs = keyArgs.map((x) => String(x[0]) + '=' + String(x[1]));
		let url = noticesURL + '?' + keyArgs.join('&');
		return getJSONReq(url);
	}

	let age_args = [[1, 18]];
	for (let i = 19; i <= 60; i++) age_args.push([i, i]);
	age_args.push([60, 120]);

	const resPerPage_arg = ['resultPerPage', 200];
	if (!singleReq) {
		let i = 0;
		for await (const cc of countryCodes) {
			const country_arg = ['nationality', cc];

			const resp = await req(country_arg, resPerPage_arg);
			let country_notices = resp._embedded.notices;
			if (country_notices.length < resp.total) {
				country_notices = [];
				for await (let id of ['F', 'M', 'U']) {
					let sd = await req(country_arg, resPerPage_arg, ['sexId', id]);
					if (sd.total > sd._embedded.notices.length) {
						for await (let age_arg of age_args) {
							let asd = await req(country_arg, resPerPage_arg, ['ageMin', age_arg[0]], ['ageMax', age_arg[1]]);
							country_notices.push(...asd._embedded.notices);
						}
					} else {
						country_notices.push(...sd._embedded.notices);
					}
				}
			}
			notices.push(...country_notices);

			i++;
			console.log(cc + '  -  ' + getPerc(i, countryCodes.length) + '% done...');
		}
	} else {
		const res = await req(resPerPage_arg);
		notices.push(...res._embedded.notices);
	}

	// Sort notices & remove duplicate notices
	console.log('Post-Processing all nodes...');
	let i = 0;

	notices = uniqSorted(notices, 'entity_id');
	const res = [];
	for await (const notice of notices) {
		const images = await getImages(notice?._links?.images?.href);
		const fullNotice = await getJSONReq(notice?._links?.self?.href);
		const normalizedNotice = normalizeNotice(fullNotice ?? notice, type, images);
		res.push(normalizedNotice);

		i++;
		console.log(getPerc(i, notices.length) + '% done..');
	}
	return res;
}

async function getRemoteData() {
	const res = [];
	for await (const type of ['red', 'yellow', 'un']) {
		console.log("Getting notices of type '" + type + "'");
		const notices = await getNoticesOfType(type, type === 'un');
		res.push(...notices);
	}
	return uniqSorted(res, 'id');
}

module.exports = getRemoteData;
