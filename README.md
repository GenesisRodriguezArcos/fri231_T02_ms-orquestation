# fri231_T02_ms-orquestation

##  Integrantes

- Génesis de los Ángeles Rodriguez Arcos  
- Luis Alberto Lopez Ordoñes  
- Victor Raul Torreblanca Franco  

---

##  Descripción del Proyecto

La Plataforma de Gestión Educativa y Comunicación Familiar es una solución digital diseñada para mejorar la comunicación entre colegios, docentes y padres de familia en instituciones públicas.

El proyecto surge ante problemas como la falta de comunicación oportuna, el registro manual de incidencias y la ausencia de evidencia en la interacción con los padres, lo que genera conflictos al final del año académico.

La plataforma permite realizar un seguimiento en tiempo real de la asistencia, conducta y desempeño académico de los estudiantes mediante notificaciones automáticas, registro digital de incidencias, historial del estudiante y comunicación directa entre el colegio y las familias.

Con esta solución se busca reducir conflictos, mejorar el rendimiento académico y fortalecer el vínculo entre la escuela y el hogar.

---

##  Tecnologías utilizadas

- Frontend: Angular 20  
- Backend: Spring Boot + WebFlux (API reactiva)  
- Base de Datos: PostgreSQL  
- Contenedores: Docker  
- Orquestación: Docker Swarm  

---

##  ¿Cómo se ejecuta el proyecto?

###  Modo desarrollo (Docker Compose)

1. Levantar los contenedores:
```bash
docker-compose up --build
```

2. Verificar que todo esté funcionando:
```bash
docker ps
```

---

###  Modo producción (Docker Swarm)

3. Inicializar Docker Swarm:
```bash
docker swarm init
```

4. Desplegar el stack:
```bash
docker stack deploy -c docker-compose.yml microservice
```

5. Verificar servicios:
```bash
docker service ls
```

6. Ver contenedores en ejecución:
```bash
docker ps
```


