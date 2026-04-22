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
  
  title = 'Edunova - Control de Tardanzas';
  
  // Data
  allStudents: Student[] = [];
  filteredStudents: Student[] = [];
  statistics: Statistics | null = null;
  selectedStudent: Student | null = null;
  studentTardiness: Tardiness[] = [];
  studentWarnings: Warning[] = [];
  
  // UI State
  loading = false;
  showCreateModal = false;
  showDetailModal = false;
  showTardinessModal = false;
  showWarningModal = false;
  
  // Filter
  currentFilter: string = 'active';
  
  // Forms
  newStudent = {
    code: '',
    dni: '',
    firstName: '',
    lastName: '',
    motherLastName: '',
    email: '',
    phone: '',
    grade: '4',
    section: 'A'
  };
  
  tardinessForm = {
    minutes: 0,
    reason: '',
    justified: false
  };
  
  warningForm = {
    type: 'L',
    reason: ''
  };

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
      error: (err) => {
        Swal.fire('Error', 'Error al cargar estudiantes: ' + err.message, 'error');
        this.loading = false;
      }
    });
  }

  loadStatistics(): void {
    this.studentService.getStatistics().subscribe({
      next: (response) => {
        if (response.success) {
          this.statistics = response.data;
        }
      },
      error: (err) => console.error('Error loading statistics:', err)
    });
  }

  applyFilter(): void {
    switch(this.currentFilter) {
      case 'active':
        this.filteredStudents = this.allStudents.filter(s => s.status === 'A');
        break;
      case 'inactive':
        this.filteredStudents = this.allStudents.filter(s => s.status === 'I');
        break;
      case 'suspended':
        this.filteredStudents = this.allStudents.filter(s => s.status === 'S');
        break;
      case 'all':
      default:
        this.filteredStudents = [...this.allStudents];
        break;
    }
  }

  setFilter(filter: string): void {
    this.currentFilter = filter;
    this.applyFilter();
  }

  getFilterCount(status: string): number {
    return this.allStudents.filter(s => s.status === status).length;
  }

  viewStudent(student: Student): void {
    this.selectedStudent = student;
    this.showDetailModal = true;
    this.loadStudentDetails(student.id);
  }

  loadStudentDetails(studentId: number): void {
    this.studentService.getTardinessByStudent(studentId).subscribe({
      next: (data) => this.studentTardiness = data,
      error: (err) => console.error('Error loading tardiness:', err)
    });
    
    this.studentService.getWarningsByStudent(studentId).subscribe({
      next: (data) => this.studentWarnings = data,
      error: (err) => console.error('Error loading warnings:', err)
    });
  }

  createStudent(): void {
    this.loading = true;
    this.studentService.createStudent(this.newStudent).subscribe({
      next: (response) => {
        if (response.success) {
          Swal.fire('Exito', response.message, 'success');
          this.loadStudents();
          this.loadStatistics();
          this.closeCreateModal();
          this.resetForms();
        }
        this.loading = false;
      },
      error: (err) => {
        Swal.fire('Error', err.error?.message || 'Error al crear estudiante', 'error');
        this.loading = false;
      }
    });
  }

  registerTardiness(): void {
    if (!this.selectedStudent) return;
    
    this.loading = true;
    this.studentService.registerTardiness(
      this.selectedStudent.id,
      this.tardinessForm.minutes,
      this.tardinessForm.reason,
      this.tardinessForm.justified
    ).subscribe({
      next: (response) => {
        if (response.success) {
          Swal.fire('Exito', response.message, 'success');
          this.loadStudentDetails(this.selectedStudent!.id);
          this.loadStatistics();
          this.closeTardinessModal();
        }
        this.loading = false;
      },
      error: (err) => {
        Swal.fire('Error', err.error?.message || 'Error al registrar tardanza', 'error');
        this.loading = false;
      }
    });
  }

  registerWarning(): void {
    if (!this.selectedStudent) return;
    
    this.loading = true;
    this.studentService.registerWarning(
      this.selectedStudent.id,
      this.warningForm.type,
      this.warningForm.reason
    ).subscribe({
      next: (response) => {
        if (response.success) {
          Swal.fire('Exito', response.message, 'success');
          this.loadStudentDetails(this.selectedStudent!.id);
          this.loadStatistics();
          this.closeWarningModal();
        }
        this.loading = false;
      },
      error: (err) => {
        Swal.fire('Error', err.error?.message || 'Error al registrar llamado', 'error');
        this.loading = false;
      }
    });
  }

  async inactivateStudent(student: Student): Promise<void> {
    const result = await Swal.fire({
      title: '¿Inactivar estudiante?',
      text: `¿Deseas inactivar a ${student.firstName} ${student.lastName}?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#ffc107',
      confirmButtonText: 'Si, inactivar',
      cancelButtonText: 'Cancelar'
    });
    
    if (result.isConfirmed) {
      this.updateStatus(student, 'I', 'inactivado');
    }
  }

  async suspendStudent(student: Student): Promise<void> {
    const result = await Swal.fire({
      title: '¿Suspender estudiante?',
      text: `¿Deseas suspender a ${student.firstName} ${student.lastName}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc3545',
      confirmButtonText: 'Si, suspender',
      cancelButtonText: 'Cancelar'
    });
    
    if (result.isConfirmed) {
      this.updateStatus(student, 'S', 'suspendido');
    }
  }

  async activateStudent(student: Student): Promise<void> {
    const result = await Swal.fire({
      title: '¿Activar estudiante?',
      text: `¿Deseas activar a ${student.firstName} ${student.lastName}?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#28a745',
      confirmButtonText: 'Si, activar',
      cancelButtonText: 'Cancelar'
    });
    
    if (result.isConfirmed) {
      this.updateStatus(student, 'A', 'activado');
    }
  }

  async deleteStudent(student: Student): Promise<void> {
    const result = await Swal.fire({
      title: '¿Eliminar estudiante?',
      text: `¿Deseas eliminar a ${student.firstName} ${student.lastName}? Esta accion no se puede deshacer.`,
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
            if (this.selectedStudent?.id === student.id) {
              this.closeDetailModal();
            }
          }
          this.loading = false;
        },
        error: (err) => {
          Swal.fire('Error', err.error?.message || 'Error al eliminar estudiante', 'error');
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
          if (this.selectedStudent?.id === student.id) {
            this.selectedStudent.status = status;
          }
        }
        this.loading = false;
      },
      error: (err) => {
        Swal.fire('Error', err.error?.message || `Error al ${action} estudiante`, 'error');
        this.loading = false;
      }
    });
  }

  getStatusText(status: string): string {
    const statusMap: Record<string, string> = { A: 'ACTIVO', I: 'INACTIVO', S: 'SUSPENDIDO' };
    return statusMap[status] || 'DESCONOCIDO';
  }

  getWarningTypeText(type: string): string {
    const typeMap: Record<string, string> = { L: 'LEVE', G: 'GRAVE', M: 'MUY GRAVE' };
    return typeMap[type] || type;
  }

  resetForms(): void {
    this.newStudent = { code: '', dni: '', firstName: '', lastName: '', motherLastName: '', email: '', phone: '', grade: '4', section: 'A' };
    this.tardinessForm = { minutes: 0, reason: '', justified: false };
    this.warningForm = { type: 'L', reason: '' };
  }

  openCreateModal(): void { this.showCreateModal = true; }
  closeCreateModal(): void { this.showCreateModal = false; this.resetForms(); }
  
  openDetailModal(student: Student): void { this.viewStudent(student); }
  closeDetailModal(): void { this.showDetailModal = false; this.selectedStudent = null; this.studentTardiness = []; this.studentWarnings = []; }
  
  openTardinessModal(): void { this.showTardinessModal = true; }
  closeTardinessModal(): void { this.showTardinessModal = false; this.tardinessForm = { minutes: 0, reason: '', justified: false }; }
  
  openWarningModal(): void { this.showWarningModal = true; }
  closeWarningModal(): void { this.showWarningModal = false; this.warningForm = { type: 'L', reason: '' }; }
}
