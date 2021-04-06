# [Fechas](https://github.com/previred/Desafio_Uno)

Crear un programa que recibe, a través de la entrada estándar, un archivo en formato Json con la estructura de la respuesta de servicio (como el ejemplo de arriba) y que entrega a través de la salida estándar, como respuesta, un archivo Json con las fechas faltantes.

Ejemplo:
Se entrega un archivo con este contenido:

```json
{
  "id": 6,
  "fechaCreacion": "1969-03-01",
  "fechaFin": "1970-01-01",
  "fechas": ["1969-03-01", "1969-05-01", "1969-09-01", "1970-01-01"]
}
```

El programa debe responder con archivo con este contenido:

```json
{
  "id": 6,
  "fechaCreacion": "1969-03-01",
  "fechaFin": "1970-01-01",
  "fechasFaltantes": [
    "1969-04-01",
    "1969-06-01",
    "1969-07-01",
    "1969-08-01",
    "1969-10-01",
    "1969-11-01",
    "1969-12-01"
  ]
}
```

## Ejecutar

`$ java -jar bin/ogu.jar -n demos/fechas/fechas.ogu`

```
In
{
    "id": 6,
    "fechaCreacion": "1969-03-01",
    "fechaFin": "1970-01-01",
    "fechas": [
        "1969-03-01",
        "1969-05-01",
        "1969-09-01",
        "1970-01-01"
    ]
}
Out
{"id":6,"fechaCreacion":"1969-03-01","fechaFin":"1970-01-01","fechasFaltantes":["1969-04-01","1969-06-01","1969-07-01","1969-08-01","1969-10-01","1969-11-01","1969-12-01"]}
```
