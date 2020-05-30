## Data Integrator
Es un proyecto desarrollado con `Java`, `Spring boot`,` Apache Camel` y `Maven` para integrar información de fuentes 
externas, que serán utilizados internamente por otros módulos o proyectos.

La estructura de carpeta es la siguiente:

    └───{User Home}
        └───data-integrator
            └───input-files
                └───{módulo}

Todo proceso de carga es registrado en la tabla `integrator_info` con los siguientes campos:
- `id` Un identificador de 36 carácteres
- `servicio` El nombre del módulo que se esta cargando.
- `createAt` Fecha de procesamiento.
- `duration` Duración del proceso de integración.
- `uploaded` Número de elementos cargados de la fuente externa.
- `registered` Número de elementos registrados.
- `excluded` Número de elementos excluidos en la integración.

| id | servicio | createAt | duration | uploaded | registered | excluded |
|--------|--------|--------|--------|--------|--------|--------|
| d1f447ab-d6cf-40c7-bc0f-8011c03c95eb | Sepomex | 2020-05-30 20:54:48 | 1348 | 143891 | 143747 | 144 |


## Módulos
- [SEPOMEX](#SEPOMEX) 

### SEPOMEX
Este módulo es la carga de los asentamientos de la república mexicana por medio del Servicio Postal de México (SEPOMEX)
el cual es proporcionado por tres opciones txt, xls o xml. para un mejor procesamiento se emplea la opción de xls en el 
data integrator, por lo que el proceso se realiza con la carga del archivo `CPdescarga.xls` el cual ubicamos en la 
estructura de carpetas como se muestra a continuación

    └───{User Home}
        └───data-integrator
            └───input-files
                └───sepomex
                    └───CPdescarga.xls
   
Se registra un detalle del proceso de integración en la tabla `integrator_info` con los siguientes campos:
- `id` Un identificador de 36 carácteres
- `createAt` Fecha de procesamiento.
- `state` El nombre del estado que fue cargando.
- `uploaded` Número de asentamientos cargados.
- `registered` Número de asentamientos registrados.
- `excluded` Número de asentamientos excluidos.
- `info_id` Identificador del registro de proceso en la tabla `integrator_info`.

| id | createAt | state | uploaded | registered | excluded |info_id |
|--------|--------|--------|--------|--------|--------|--------|
| 1 | 2020-05-30 20:55:04 | Aguascalientes | 1355 | 1355 | 0 | d1f447ab-d6cf-40c7-bc0f-8011c03c95eb|
|...|
| 32 | 2020-05-30 21:14:50 | Zacatecas | 1775 | 1775 | 0 | d1f447ab-d6cf-40c7-bc0f-8011c03c95eb|


#### Fuente
- [Servicio Postal de México](https://www.correosdemexico.gob.mx/SSLServicios/ConsultaCP/CodigoPostal_Exportar.aspx)


## Build
La construcción del proyecto se hace mediante *Maven*. para ello a la altura de la raiz del proyecto se puede ejecutar 
el comando para construir el proyecto.
```
mvnw clear compile
```

Pueden instalar el jar en el repositorio local con el comando 
```
mvnw clean install
```

## Run
Para ejecutar el proyecto se hace mediante *Maven*. para ello a la altura de la raiz del proyecto se puede ejecutar 
el comando.
```
mvnw clean spring-boot:run
```