const express = require('express');
const mysql = require('mysql');
const { MongoClient } = require('mongodb');
const bodyParser = require('body-parser');
const app = express();
const port = 3001;

app.use(bodyParser.json());
const mysqlDB = mysql.createConnection({
  host: 'localhost',
  user: 'student',
  password: 'password_stud',
  database: 'picture_database',
});

mysqlDB.connect((err) => {
  if (err) {
    console.error('Eroare la conectarea la MySQL: ', err);
    return;
  }
  console.log('Conectat la MySQL');
});

const mongoUri = 'mongodb://localhost:27017';
let db;

MongoClient.connect(mongoUri, { useUnifiedTopology: true })
  .then(client => {
    db = client.db('snmp_db');
    console.log('Conectat la MongoDB');
  })
  .catch(err => {
    console.error('Eroare la conectarea la MongoDB: ', err);
  });
app.post('/snmp', async (req, res) => {
  try {
    const { osName, cpuUsage, ramUsage } = req.body;
    const snmpCollection = db.collection('snmp_data');
    
    const newSnmpData = {
      osName,
      cpuUsage,
      ramUsage,
      timestamp: new Date(),
    };
    await snmpCollection.insertOne(newSnmpData);
    res.status(201).json({ message: 'Datele SNMP s-au salvat cu succes!' });
  } catch (err) {
    res.status(500).json({ message: 'Eroare la salvarea datelor' });
  }
});
app.get('/snmp', async (req, res) => {
  try {
    const snmpCollection = db.collection('snmp_data');
    const snmpValues = await snmpCollection.find({}).toArray();
    res.json(snmpValues);
  } catch (err) {
    res.status(500).json({ message: 'Eroare la salvarea datelor' });
  }
});
app.post('/upload-image', (req, res) => {
  const { imageBytes, description } = req.body;

  if (!imageBytes || !description) {
    return res.status(400).json({ message: 'Este necesar să fie furnizate atât datele imaginii, cât și descrierea.' });
  }

  const fileName = 'Imagine.bmp';
  const fileType = 'image/bmp';
  const fileSize = imageBytes.length;
  const query = 'INSERT INTO pictures (file_name, file_type, file_size, image, description) VALUES (?, ?, ?, ?, ?)';
  
  mysqlDB.query(query, [fileName, fileType, fileSize, imageBytes, description], (err, results) => {
    if (err) {
      console.error('Eroare la salvarea imaginii: ', err);
      return res.status(500).json({ message: 'Eroare' });
    }
    res.status(200).json({ message: 'Imagine incarcata si salvata', imageId: results.insertId });
  });
});
app.get('/image/:id', (req, res) => {
  const imageId = req.params.id;
  const query = 'SELECT image, file_type FROM pictures WHERE id = ?';
  
  mysqlDB.query(query, [imageId], (err, results) => {
    if (err) {
      console.error('Eroare la recuperarea imaginii: ', err);
      return res.status(500).json({ message: 'Eroare' });
    }
    
    if (results.length > 0) {
      const image = results[0].image;
      const fileType = results[0].file_type;
      res.contentType(fileType);
      res.send(image);
    } else {
      res.status(404).send('Imaginea nu este gasita');
    }
  });
});

app.listen(port, () => {
  console.log(`Serverul API ruleaza la portul http://localhost:${port}`);
});
