cd ~/Downloads/fri231_T02_ms-orquestation

cat > README.md << 'EOF'
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

Con esta solución se busca reducir conflictos, mejorar el rendimiento académico y fortalecer el vínculo entre la escuela y el hogar.

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

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (con Swarm habilitado)
- [Node.js](https://nodejs.org/) (v18 o superior)
- [Angular CLI](https://angular.io/cli) (`npm install -g @angular/cli`)
- [Git](https://git-scm.com/)
- [Java 17](https://adoptium.net/) (solo para desarrollo local)

---

## 🚀 ¿Cómo ejecutar el proyecto?

### 🔧 Modo desarrollo (Backend local + Frontend local)

```bash
# 1. Clonar el repositorio
git clone https://github.com/GenesisRodriguezArcos/fri231_T02_ms-orquestation.git
cd fri231_T02_ms-orquestation

# 2. Levantar solo la base de datos
docker-compose up -d database

# 3. Ejecutar el backend (en otra terminal)
./mvnw spring-boot:run

# 4. Ejecutar el frontend (en otra terminal)
cd edunova-frontend
npm install
ng serve --port 4200
