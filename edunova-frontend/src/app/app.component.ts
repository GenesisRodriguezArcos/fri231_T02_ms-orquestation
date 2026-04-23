import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { StudentService } from './services/student.service';
import { Student, Statistics, Tardiness, Warning } from './models/student.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  private studentService = inject(StudentService);
  
  allStudents: Student[] = [];
  filteredStudents: Student[] = [];
  statistics: Statistics | null = null;
  selectedStudent: Student | null = null;
  studentTardiness: Tardiness[] = [];
  studentWarnings: Warning[] = [];
  
  loading = false;
  showCreateModal = false;
  showDetailModal = false;
  showEditModal = false;
  showTardinessModal = false;
  showWarningModal = false;
  currentFilter: string = 'active';
  
  secciones = ['A', 'B', 'C', 'D', 'E'];
  grados = ['1', '2', '3', '4', '5'];
  
  newStudent = { 
    dni: '', 
    firstName: '', 
    lastName: '', 
    motherLastName: '', 
    email: '', 
    phone: '', 
    grade: '4', 
    section: 'A' 
  };
  
  editStudent = { 
    id: 0, 
    firstName: '', 
    lastName: '', 
    motherLastName: '', 
    email: '', 
    phone: '', 
    grade: '4', 
    section: 'A' 
  };
  
  tardinessForm = { minutes: 0, reason: '', justified: false };
  warningForm = { type: 'L', reason: '' };

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.loadStudents();
    this.loadStatistics();
  }

  loadStudents(): void {
    this.loading = true;
    this.studentService.getStudents().subscribe({
      next: (data) => {
        this.allStudents = data;
        this.applyFilter();
        this.loading = false;
      },
      error: () => {
        Swal.fire('Error', 'Error al cargar estudiantes', 'error');
        this.loading = false;
      }
    });
  }

  loadStatistics(): void {
    this.studentService.getStatistics().subscribe({
      next: (response) => {
        if (response.success) this.statistics = response.data;
      }
    });
  }

  applyFilter(): void {
    switch(this.currentFilter) {
      case 'active': this.filteredStudents = this.allStudents.filter(s => s.status === 'A'); break;
      case 'inactive': this.filteredStudents = this.allStudents.filter(s => s.status === 'I'); break;
      case 'suspended': this.filteredStudents = this.allStudents.filter(s => s.status === 'S'); break;
      default: this.filteredStudents = [...this.allStudents]; break;
    }
  }

  setFilter(filter: string): void {
    this.currentFilter = filter;
    this.applyFilter();
  }

  viewStudent(student: Student): void {
    this.selectedStudent = student;
    this.showDetailModal = true;
    this.studentService.getTardinessByStudent(student.id).subscribe({
      next: (data) => this.studentTardiness = data
    });
    this.studentService.getWarningsByStudent(student.id).subscribe({
      next: (data) => this.studentWarnings = data
    });
  }

  openEditModal(student: Student): void {
    this.editStudent = {
      id: student.id,
      firstName: student.firstName,
      lastName: student.lastName,
      motherLastName: student.motherLastName || '',
      email: student.email,
      phone: student.phone || '',
      grade: student.grade,
      section: student.section
    };
    this.showEditModal = true;
  }

  updateStudent(): void {
    this.loading = true;
    this.studentService.updateStudent(this.editStudent.id, {
      firstName: this.editStudent.firstName,
      lastName: this.editStudent.lastName,
      motherLastName: this.editStudent.motherLastName,
      email: this.editStudent.email,
      phone: this.editStudent.phone,
      grade: this.editStudent.grade,
      section: this.editStudent.section
    }).subscribe({
      next: (response) => {
        if (response.success) {
          Swal.fire('Exito', response.message, 'success');
          this.loadStudents();
          this.closeEditModal();
          if (this.selectedStudent?.id === this.editStudent.id) {
            this.viewStudent(this.editStudent as Student);
          }
        }
        this.loading = false;
      },
      error: () => {
        Swal.fire('Error', 'Error al actualizar estudiante', 'error');
        this.loading = false;
      }
    });
  }

  createStudent(): void {
    if (!this.newStudent.dni || this.newStudent.dni.length !== 8 || !/^\d+$/.test(this.newStudent.dni)) {
      Swal.fire('Error', 'El DNI debe tener 8 digitos numericos', 'error');
      return;
    }
    if (this.newStudent.phone && !/^9\d{8}$/.test(this.newStudent.phone)) {
      Swal.fire('Error', 'El telefono debe comenzar con 9 y tener 9 digitos', 'error');
      return;
    }
    
    this.loading = true;
    this.studentService.createStudent({
      dni: this.newStudent.dni,
      firstName: this.newStudent.firstName,
      lastName: this.newStudent.lastName,
      motherLastName: this.newStudent.motherLastName,
      email: this.newStudent.email,
      phone: this.newStudent.phone,
      grade: this.newStudent.grade,
      section: this.newStudent.section
    }).subscribe({
      next: (response) => {
        if (response.success) {
          Swal.fire('Exito', response.message, 'success');
          this.loadStudents();
          this.loadStatistics();
          this.closeCreateModal();
        }
        this.loading = false;
      },
      error: () => {
        Swal.fire('Error', 'Error al crear estudiante', 'error');
        this.loading = false;
      }
    });
  }

  registerTardiness(): void {
    if (!this.selectedStudent) return;
    this.loading = true;
    this.studentService.registerTardiness(this.selectedStudent.id, this.tardinessForm.minutes, this.tardinessForm.reason, this.tardinessForm.justified).subscribe({
      next: (response) => {
        if (response.success) {
          Swal.fire('Exito', response.message, 'success');
          this.viewStudent(this.selectedStudent!);
          this.loadStatistics();
          this.closeTardinessModal();
        }
        this.loading = false;
      },
      error: () => {
        Swal.fire('Error', 'Error al registrar tardanza', 'error');
        this.loading = false;
      }
    });
  }

  registerWarning(): void {
    if (!this.selectedStudent) return;
    this.loading = true;
    this.studentService.registerWarning(this.selectedStudent.id, this.warningForm.type, this.warningForm.reason).subscribe({
      next: (response) => {
        if (response.success) {
          Swal.fire('Exito', response.message, 'success');
          this.viewStudent(this.selectedStudent!);
          this.loadStatistics();
          this.closeWarningModal();
        }
        this.loading = false;
      },
      error: () => {
        Swal.fire('Error', 'Error al registrar llamado', 'error');
        this.loading = false;
      }
    });
  }

  async desactivarStudent(student: Student): Promise<void> {
    const result = await Swal.fire({
      title: '¿Desactivar estudiante?',
      text: `¿Deseas desactivar a ${student.firstName} ${student.lastName}?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#ffc107',
      confirmButtonText: 'Si, desactivar',
      cancelButtonText: 'Cancelar'
    });
    if (result.isConfirmed) this.updateStatus(student, 'I', 'desactivado');
  }

  async suspenderStudent(student: Student): Promise<void> {
    const result = await Swal.fire({
      title: '¿Suspender estudiante?',
      text: `¿Deseas suspender a ${student.firstName} ${student.lastName}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc3545',
      confirmButtonText: 'Si, suspender',
      cancelButtonText: 'Cancelar'
    });
    if (result.isConfirmed) this.updateStatus(student, 'S', 'suspendido');
  }

  async activarStudent(student: Student): Promise<void> {
    const result = await Swal.fire({
      title: '¿Activar estudiante?',
      text: `¿Deseas activar a ${student.firstName} ${student.lastName}?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#28a745',
      confirmButtonText: 'Si, activar',
      cancelButtonText: 'Cancelar'
    });
    if (result.isConfirmed) this.updateStatus(student, 'A', 'activado');
  }

  async eliminarStudent(student: Student): Promise<void> {
    const result = await Swal.fire({
      title: '¿Eliminar estudiante?',
      text: `¿Eliminar a ${student.firstName} ${student.lastName}? No se puede deshacer.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc3545',
      confirmButtonText: 'Si, eliminar',
      cancelButtonText: 'Cancelar'
    });
    if (result.isConfirmed) {
      this.loading = true;
      this.studentService.deleteStudent(student.id).subscribe({
        next: (response) => {
          if (response.success) {
            Swal.fire('Eliminado', response.message, 'success');
            this.loadStudents();
            this.loadStatistics();
            if (this.selectedStudent?.id === student.id) this.closeDetailModal();
          }
          this.loading = false;
        },
        error: () => {
          Swal.fire('Error', 'Error al eliminar estudiante', 'error');
          this.loading = false;
        }
      });
    }
  }

  private updateStatus(student: Student, status: string, action: string): void {
    this.loading = true;
    this.studentService.updateStatus(student.id, status).subscribe({
      next: (response) => {
        if (response.success) {
          Swal.fire('Exito', response.message, 'success');
          this.loadStudents();
          this.loadStatistics();
          if (this.selectedStudent?.id === student.id) this.selectedStudent.status = status;
        }
        this.loading = false;
      },
      error: () => {
        Swal.fire('Error', `Error al ${action} estudiante`, 'error');
        this.loading = false;
      }
    });
  }

  getStatusText(status: string): string {
    return status === 'A' ? 'ACTIVO' : status === 'I' ? 'DESACTIVADO' : 'SUSPENDIDO';
  }

  getWarningTypeText(type: string): string {
    return type === 'L' ? 'LEVE' : type === 'G' ? 'GRAVE' : 'MUY GRAVE';
  }

  openCreateModal(): void { 
    this.newStudent = { dni: '', firstName: '', lastName: '', motherLastName: '', email: '', phone: '', grade: '4', section: 'A' };
    this.showCreateModal = true; 
  }
  closeCreateModal(): void { this.showCreateModal = false; }
  closeDetailModal(): void { this.showDetailModal = false; this.selectedStudent = null; }
  closeEditModal(): void { this.showEditModal = false; }
  openTardinessModal(): void { this.tardinessForm = { minutes: 0, reason: '', justified: false }; this.showTardinessModal = true; }
  closeTardinessModal(): void { this.showTardinessModal = false; }
  openWarningModal(): void { this.warningForm = { type: 'L', reason: '' }; this.showWarningModal = true; }
  closeWarningModal(): void { this.showWarningModal = false; }
}
