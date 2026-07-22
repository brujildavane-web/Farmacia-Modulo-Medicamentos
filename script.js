import http from "node:http";
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));

const servidor = http.createServer((req, res) => {
    // Si el usuario entra a la ruta principal, enviamos el HTML de la farmacia
    if (req.url === "/" || req.url === "/index.html") {
        fs.readFile(path.join(__dirname, "public", "mod.html"), (err, content) => {
            res.writeHead(200, { "Content-Type": "text/html; charset=utf-8" });
            res.end(content);
        });
    } 
    // Respuesta JSON básica como pide la guía del profesor
    else if (req.url === "/estado") {
        res.writeHead(200, { "Content-Type": "application/json; charset=utf-8" });
        const respuesta = {
            mensaje: "Servidor de la Farmacia funcionando con Node.js nativo",
            metodo: req.method,
            ruta: req.url
        };
        res.end(JSON.stringify(respuesta));
    }
});

servidor.listen(3000, () => {
    console.log("Servidor ejecutándose en http://localhost:3000");
});