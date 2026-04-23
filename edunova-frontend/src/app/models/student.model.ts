export interface Student {
  id: number;
  code: string;
  dni: string;
  firstName: string;
  lastName: string;
  motherLastName: string;
  email: string;
  phone: string;
  grade: string;
  section: string;
  status: string;
  registrationDate?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  code: string;
  timestamp: string;
  data: T;
}

export interface Statistics {
  totalStudents: number;
  totalTardiness: number;
  totalWarnings: number;
  activeStudents: number;
  inactiveStudents: number;
  suspendedStudents: number;
}

export interface Tardiness {
  id: number;
  studentId: number;
  studentName: string;
  date: string;
  arrivalTime: string;
  minutesLate: number;
  justified: boolean;
  reason: string;
  registeredBy: string;
}

export interface Warning {
  id: number;
  studentId: number;
  studentName: string;
  date: string;
  type: string;
  typeDescription: string;
  reason: string;
  registeredBy: string;
}

export interface Section {
  value: string;
  label: string;
}
