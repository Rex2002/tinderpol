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
let lastUpdated = Date.now(); // To force an immediate update, set to 0
let notices = [];

// TODO:
// Sort the retrieved Data by entity_id
// Then respond with the current entity_id instead of the current cursor
// That way pagination should consist to work, even if the data changed

async function getData() {
	if (!fs.existsSync(DATA_FILE) || Date.now() > lastUpdated + DATA_LIFETIME) {
		notices = await getRemoteData();
		return new Promise((resolve, reject) => {
			const json = JSON.stringify(notices, null, 4);
			fs.writeFile(DATA_FILE, json, { encoding: 'utf-8' }, (err) => {
				if (err) reject(err);
				lastUpdated = Date.now();
				console.log('Data updated and written to file');
				resolve(notices);
			});
		});
	} else return new Promise((resolve, reject) => resolve(notices));
}

app.get('/all', async (req, res) => {
	const data = await getData();
	const resData = { notices: data };
	res.send(resData);
}).get('/data', async (req, res) => {
	const cursor = Number(req.query?.page || 0);
	const data = await getData();
	const idx = PAGE_SIZE * cursor;
	const maxCursor = Math.floor(data.length / PAGE_SIZE);
	const nextCursor = cursor >= maxCursor ? maxCursor : cursor + 1;

	const resData = { max_cursor: maxCursor, notices: [], current_page: cursor, next_page: nextCursor };
	if (idx < data.length) resData.notices = data.slice(idx, idx + PAGE_SIZE);
	res.send(resData);
});

getData().then(() => {
	setInterval(() => {
		getData();
	}, DATA_LIFETIME);
});

app.listen(PORT, () => console.log(`Server listening on Port ${PORT}`));
