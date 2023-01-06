const http = require('https');
const fs = require('fs');

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
				if (statusCode === 403) {
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

async function getRemoteData() {
	const countryCodes = await getCountryCodes();
	const notices = [];
	const noticesURL = 'https://ws-public.interpol.int/notices/v1/red';

	async function req(...keyArgs) {
		keyArgs = keyArgs.map((x) => String(x[0]) + '=' + String(x[1]));
		let url = noticesURL + '?' + keyArgs.join('&');
		return getJSONReq(url);
	}

	let age_args = [[1, 18]];
	for (let i = 19; i <= 60; i++) age_args.push([i, i]);
	age_args.push([60, 120]);

	let i = 0;
	for await (const cc of countryCodes) {
		const country_arg = ['nationality', cc];
		const resPerPage_arg = ['resultsPerPage', 200];

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

		console.log(cc + '  -  ' + getPerc(i, countryCodes.length) + '% done...');
		i++;
	}

	// Sort notices
	notices.sort((a, b) => a.entity_id - b.entity_id);
	// Remove duplicate notices
	return notices.filter((notice, idx, arr) => !idx || notice != arr[idx - 1]);
}

module.exports = getRemoteData;
