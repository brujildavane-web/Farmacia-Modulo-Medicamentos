import mysql from 'mysql2';

const conexionFarmacia = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'bd_farmacia'
});

export default conexionFarmacia; 