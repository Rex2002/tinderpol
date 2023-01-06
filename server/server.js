const fs = require('fs');
const path = require('path');
const express = require('express');
const app = express();
const getRemoteData = require('./remoteData.js');

app.use(express.json());

const PORT = process.env.PORT || 80;
const PAGE_SIZE = 100;
const DATA_FILE = path.join(__dirname, 'data.json');
const DATA_LIFETIME = 1000 * 60 * 60 * 15; // 15 days lifetime
let isCurrentlyUpdating = false;
let lastUpdated = 0;
let notices = [];

// TODO:
// Sort the retrieved Data by entity_id
// Then respond with the current entity_id instead of the current cursor
// That way pagination should consist to work, even if the data changed

async function getData(forceUpdate = false) {
	if (forceUpdate || !fs.existsSync(DATA_FILE)) {
		if (isCurrentlyUpdating) Promise.reject(new Error('The Data is currently being updated. Please try again soon.'));
		else {
			isCurrentlyUpdating = true;
			notices = await getRemoteData();
			return new Promise((resolve, reject) => {
				const json = JSON.stringify(notices, null, 4);
				fs.writeFile(DATA_FILE, json, { encoding: 'utf-8' }, (err) => {
					if (err) {
						console.error('Something went wrong, trying to save the newly updated data:');
						console.error(err);
					}
					lastUpdated = Date.now();
					isCurrentlyUpdating = false;
					console.log('Data updated and written to file');
					resolve(notices);
				});
			});
		}
	} else if (!notices || notices.length === 0) {
		return new Promise((resolve, reject) => {
			fs.readFile(DATA_FILE, { encoding: 'utf-8' }, (err, data) => {
				if (err) {
					console.error('Something went wrong, trying to read the data from disk:');
					console.error(err);
					reject(new Error('An Error occured when fetching the error. Please try again in a few minutes.'));
				}
				const json = JSON.parse(data);
				resolve(json);
			});
		});
	} else return Promise.resolve(notices);
}

app.get('/', (req, res) => {
	res.redirect('/all');
})
	.get('/all', (req, res) => {
		let resData = { data: [], err: null, lastUpdate: lastUpdated };
		let status = 200;
		getData()
			.then((data) => {
				resData.data = data;
			})
			.catch((err) => {
				resData.err = err.message;
				status = 500;
			})
			.finally(() => {
				res.status(status).json(resData);
			});
	})
	.get('/data', async (req, res) => {
		const cursor = Number(req.query?.page || 0);
		const data = await getData();
		const idx = PAGE_SIZE * cursor;
		const maxCursor = Math.floor(data.length / PAGE_SIZE);
		const nextCursor = cursor >= maxCursor ? maxCursor : cursor + 1;

		const resData = { max_cursor: maxCursor, notices: [], current_page: cursor, next_page: nextCursor };
		if (idx < data.length) resData.notices = data.slice(idx, idx + PAGE_SIZE);
		res.send(resData);
	});

getData(true).then(() => {
	setInterval(() => {
		getData(true);
	}, DATA_LIFETIME);
});

app.listen(PORT, () => console.log(`Server listening on Port ${PORT}`));
