import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
  
  title = 'Edunova - Sistema de Control de Tardanzas';
  
  // Data
  students: Student[] = [];
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
  message: { text: string; type: 'success' | 'error' } | null = null;
  
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
        this.students = data;
        this.loading = false;
      },
      error: (err) => {
        this.showMessage('Error al cargar estudiantes: ' + err.message, 'error');
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
          this.showMessage(response.message, 'success');
          this.loadStudents();
          this.loadStatistics();
          this.closeCreateModal();
          this.resetForms();
        }
        this.loading = false;
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Error al crear estudiante', 'error');
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
          this.showMessage(response.message, 'success');
          this.loadStudentDetails(this.selectedStudent!.id);
          this.loadStatistics();
          this.closeTardinessModal();
        }
        this.loading = false;
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Error al registrar tardanza', 'error');
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
          this.showMessage(response.message, 'success');
          this.loadStudentDetails(this.selectedStudent!.id);
          this.loadStatistics();
          this.closeWarningModal();
        }
        this.loading = false;
      },
      error: (err) => {
        this.showMessage(err.error?.message || 'Error al registrar llamado', 'error');
        this.loading = false;
      }
    });
  }

  updateStatus(student: Student, status: string): void {
    const statusText = status === 'A' ? 'activar' : status === 'I' ? 'inactivar' : 'suspender';
    if (confirm(`¿Está seguro de ${statusText} a ${student.firstName} ${student.lastName}?`)) {
      this.loading = true;
      this.studentService.updateStatus(student.id, status).subscribe({
        next: (response) => {
          if (response.success) {
            this.showMessage(response.message, 'success');
            this.loadStudents();
            this.loadStatistics();
          }
          this.loading = false;
        },
        error: (err) => {
          this.showMessage(err.error?.message || 'Error al cambiar estado', 'error');
          this.loading = false;
        }
      });
    }
  }

  deleteStudent(student: Student): void {
    if (confirm(`¿Eliminar a ${student.firstName} ${student.lastName}? Esta acción no se puede deshacer.`)) {
      this.loading = true;
      this.studentService.deleteStudent(student.id).subscribe({
        next: (response) => {
          if (response.success) {
            this.showMessage(response.message, 'success');
            this.loadStudents();
            this.loadStatistics();
            if (this.selectedStudent?.id === student.id) {
              this.closeDetailModal();
            }
          }
          this.loading = false;
        },
        error: (err) => {
          this.showMessage(err.error?.message || 'Error al eliminar estudiante', 'error');
          this.loading = false;
        }
      });
    }
  }

  getStatusText(status: string): string {
    const statusMap: Record<string, string> = { A: 'ACTIVO', I: 'INACTIVO', S: 'SUSPENDIDO' };
    return statusMap[status] || 'DESCONOCIDO';
  }

  getStatusClass(status: string): string {
    const classMap: Record<string, string> = { A: 'status-active', I: 'status-inactive', S: 'status-suspended' };
    return classMap[status] || '';
  }

  getWarningTypeText(type: string): string {
    const typeMap: Record<string, string> = { L: 'LEVE', G: 'GRAVE', M: 'MUY GRAVE' };
    return typeMap[type] || type;
  }

  showMessage(text: string, type: 'success' | 'error'): void {
    this.message = { text, type };
    setTimeout(() => { this.message = null; }, 5000);
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
