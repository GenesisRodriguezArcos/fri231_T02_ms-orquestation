# fri231_T02_ms-orquestation

## 👥 Integrantes

- Génesis de los Ángeles Rodriguez Arcos  
- Luis Alberto Lopez Ordoñes  
- Victor Raul Torreblanca Franco  

---

## 📖 Descripción del Proyecto

La Plataforma de Gestión Educativa y Comunicación Familiar es una solución digital diseñada para mejorar la comunicación entre colegios, docentes y padres de familia en instituciones públicas.

El proyecto surge ante problemas como la falta de comunicación oportuna, el registro manual de incidencias y la ausencia de evidencia en la interacción con los padres, lo que genera conflictos al final del año académico.

La plataforma permite realizar un seguimiento en tiempo real de la asistencia, conducta y desempeño académico de los estudiantes mediante notificaciones automáticas, registro digital de incidencias, historial del estudiante y comunicación directa entre el colegio y las familias.

---

## 🛠️ Tecnologías utilizadas

| Capa | Tecnología |
|------|------------|
| Frontend | Angular 20 (Standalone) |
| Backend | Spring Boot + WebFlux (API reactiva) |
| Base de Datos | PostgreSQL 15 |
| Contenedores | Docker |
| Orquestación | Docker Swarm |
| Iconos | Bootstrap Icons |
| Alertas | SweetAlert2 |

---

## 📋 Prerrequisitos

- Docker Desktop (con Swarm habilitado)
- Node.js (v18 o superior)
- Angular CLI (`npm install -g @angular/cli`)
- Git
- Java 17 (solo para desarrollo local)

---

## 🚀 Despliegue con Docker Swarm

```bash
# 1. Clonar el repositorio
git clone https://github.com/GenesisRodriguezArcos/fri231_T02_ms-orquestation.git
cd fri231_T02_ms-orquestation

# 2. Construir la imagen del backend
docker build -t edunova-backend:latest .

# 3. Inicializar Docker Swarm
docker swarm init

# 4. Desplegar el stack completo
docker stack deploy -c docker-compose.yml edunova

# 5. Verificar servicios
docker service ls
docker service ps edunova_backend

# 6. Ver logs
docker service logs edunova_backend --tail 20
