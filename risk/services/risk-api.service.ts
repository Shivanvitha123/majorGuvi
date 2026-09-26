import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


import {
  RiskAssessment,
  RiskAssessmentRequest,
  Simulation,
  SimulationRequest
} from '../models/risk.models';
import { environment } from '../../../../environments/environments';

@Injectable({
  providedIn: 'root'
})
export class RiskApiService {

  private readonly http = inject(HttpClient);

  private readonly assessmentUrl =
    `${environment.apiUrl}/api/risk/assessments`;

  private readonly simulationUrl =
    `${environment.apiUrl}/api/risk/simulations`;

  createAssessment(
    request: RiskAssessmentRequest
  ): Observable<RiskAssessment> {

    return this.http.post<RiskAssessment>(
      this.assessmentUrl,
      request
    );
  }

  getAssessment(
    id: number
  ): Observable<RiskAssessment> {

    return this.http.get<RiskAssessment>(
      `${this.assessmentUrl}/${id}`
    );
  }

  getByBusiness(
    businessId: number
  ): Observable<RiskAssessment[]> {

    return this.http.get<RiskAssessment[]>(
      `${this.assessmentUrl}/business/${businessId}`
    );
  }

  createSimulation(
    request: SimulationRequest
  ): Observable<Simulation> {

    return this.http.post<Simulation>(
      this.simulationUrl,
      request
    );
  }

  getSimulation(
    id: number
  ): Observable<Simulation> {

    return this.http.get<Simulation>(
      `${this.simulationUrl}/${id}`
    );
  }

  getSimulations(): Observable<Simulation[]> {

    return this.http.get<Simulation[]>(
      this.simulationUrl
    );
  }
}
