const express = require('express');
const mysql = require('mysql');
const WebSocket = require('ws');
const app = express();
const port = 3002;

const mysqlDB = mysql.createConnection({
  host: 'localhost:3306',
  user: 'student',
  password: 'password_stud',
  database: 'picture_database'
});

const wss = new WebSocket.Server({ noServer: true });

wss.on('connection', (ws) => {
  console.log('Clientul s-a conectat prin WebSocket');
  ws.on('message', (message) => {
    console.log('Received message: ', message);
  });
});

app.use(express.json());

app.get('/image/:id', (req, res) => {
  const imageId = req.params.id;
  mysqlDB.execute('SELECT image FROM pictures WHERE id = ?', [imageId], (err, results) => {
    if (err) {
      return res.status(500).json({ message: 'Eroare' });
    }
    if (results.length > 0) {
      const image = results[0].image;
      res.contentType('image/bmp');
      res.send(image);
    } else {
      res.status(404).send('Imaginea nu este gasita!');
    }
  });
});

function notifyFrontend(imageId) {
  wss.clients.forEach((client) => {
    if (client.readyState === WebSocket.OPEN) {
      client.send(JSON.stringify({ action: 'imageReady', imageId: imageId }));
    }
  });
}

app.post('/notify-image-upload', (req, res) => {
  const { imageId } = req.body;
  notifyFrontend(imageId);
  res.status(200).json({ message: 'Notificarea este trimisa catre frontend' });
});

const server = app.listen(port, () => {
  console.log(`Serverul web ruleaza la portul http://localhost:${port}`);
});

server.on('upgrade', (request, socket, head) => {
  wss.handleUpgrade(request, socket, head, (ws) => {
    wss.emit('connection', ws, request);
  });
});
