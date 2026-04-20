import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Student, ApiResponse, Statistics, Tardiness, Warning, Report } from '../models/student.model';

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  // Students
  getStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.apiUrl}/students`);
  }

  getStudent(id: number): Observable<Student> {
    return this.http.get<Student>(`${this.apiUrl}/students/${id}`);
  }

  createStudent(student: Partial<Student>): Observable<ApiResponse<Student>> {
    return this.http.post<ApiResponse<Student>>(`${this.apiUrl}/students`, student);
  }

  updateStudent(id: number, student: Partial<Student>): Observable<ApiResponse<Student>> {
    return this.http.put<ApiResponse<Student>>(`${this.apiUrl}/students/${id}`, student);
  }

  deleteStudent(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/students/${id}`);
  }

  updateStatus(id: number, status: string): Observable<ApiResponse<Student>> {
    return this.http.patch<ApiResponse<Student>>(`${this.apiUrl}/students/${id}/status?status=${status}`, {});
  }

  // Tardiness
  registerTardiness(studentId: number, minutes: number, reason: string, justified: boolean): Observable<ApiResponse<Tardiness>> {
    return this.http.post<ApiResponse<Tardiness>>(`${this.apiUrl}/students/${studentId}/tardiness`, {
      minutes,
      reason,
      justified
    });
  }

  getTardinessByStudent(studentId: number): Observable<Tardiness[]> {
    return this.http.get<Tardiness[]>(`${this.apiUrl}/students/${studentId}/tardiness`);
  }

  getAllTardiness(): Observable<Tardiness[]> {
    return this.http.get<Tardiness[]>(`${this.apiUrl}/tardiness`);
  }

  // Warnings
  registerWarning(studentId: number, type: string, reason: string): Observable<ApiResponse<Warning>> {
    return this.http.post<ApiResponse<Warning>>(`${this.apiUrl}/students/${studentId}/warning`, {
      type,
      reason
    });
  }

  getWarningsByStudent(studentId: number): Observable<Warning[]> {
    return this.http.get<Warning[]>(`${this.apiUrl}/students/${studentId}/warnings`);
  }

  getAllWarnings(): Observable<Warning[]> {
    return this.http.get<Warning[]>(`${this.apiUrl}/warnings`);
  }

  // Statistics & Reports
  getStatistics(): Observable<ApiResponse<Statistics>> {
    return this.http.get<ApiResponse<Statistics>>(`${this.apiUrl}/statistics`);
  }

  getStudentReport(studentId: number): Observable<ApiResponse<Report>> {
    return this.http.get<ApiResponse<Report>>(`${this.apiUrl}/students/${studentId}/report`);
  }

  healthCheck(): Observable<ApiResponse<string>> {
    return this.http.get<ApiResponse<string>>(`${this.apiUrl}/health`);
  }
}
