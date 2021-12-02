# NumBot
Servicio REST que provee el número de Boletin Oficial de la Comunidad de Madrid correspondiente a una fecha determinada.

## Introducción
El Boletín Oficial de la Comunidad de Madrid se publica de lunes a sábado, incluyendo festivos, excepto:

* Año nuevo (1 de enero).
* Navidad (25 de diciembre).
* Viernes Santo (variable, se puede obtener a partir del [algoritmo de Gauss](https://en.wikipedia.org/wiki/Date_of_Easter#Gauss's_Easter_algorithm) para calcular la fecha del domingo de Pascua).

Además, hay que tener en cuenta que en ocasiones extraordinarias se publican dos boletines en el mismo día.

## Motivación
Se usa como apoyo en aplicaciones y scripts de uso interno. Principalmente, se utiliza para comprobar que todos los datos generados en la elaboración de los boletines estén almacenados en su ubicación final permanente, evitando su pérdida. Entre estos datos se incluyen:

* Ficheros de los distintos formatos en que se publica el Boletín, actualmente PDF, HTML y EPUB.
* Ficheros generados por el software utilizado durante la elaboración de los boletines.
* Entradas en la base de datos.
