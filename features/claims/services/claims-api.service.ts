import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';



import {
  Claim,
  CreateClaimRequest,
  UpdateClaimStatusRequest,
  Recovery,
  CreateRecoveryRequest,
  UpdateRecoveryStatusRequest
} from '../models/claims.models';
import { environment } from '../../../../environments/environments';

@Injectable({
  providedIn: 'root'
})
export class ClaimsApiService {

  private readonly http = inject(HttpClient);

  private readonly baseUrl =
    `${environment.apiUrl}/api/claims`;

  // ---------------- CLAIMS ----------------

  createClaim(
    request: CreateClaimRequest
  ): Observable<Claim> {
    return this.http.post<Claim>(
      this.baseUrl,
      request
    );
  }

  getAllClaims(): Observable<Claim[]> {
    return this.http.get<Claim[]>(
      this.baseUrl
    );
  }

  getOwnerClaims(): Observable<Claim[]> {
    return this.http.get<Claim[]>(
      `${this.baseUrl}/owner`
    );
  }

  getClaim(
    claimId: number
  ): Observable<Claim> {
    return this.http.get<Claim>(
      `${this.baseUrl}/${claimId}`
    );
  }

  updateClaimStatus(
    claimId: number,
    request: UpdateClaimStatusRequest
  ): Observable<Claim> {
    return this.http.patch<Claim>(
      `${this.baseUrl}/${claimId}/status`,
      request
    );
  }

  // ---------------- RECOVERY ----------------

  createRecovery(
    claimId: number,
    request: CreateRecoveryRequest
  ): Observable<Recovery> {
    return this.http.post<Recovery>(
      `${this.baseUrl}/${claimId}/recovery`,
      request
    );
  }

  getClaimRecoveries(
    claimId: number
  ): Observable<Recovery[]> {
    return this.http.get<Recovery[]>(
      `${this.baseUrl}/${claimId}/recovery`
    );
  }

  getAllRecoveries(): Observable<Recovery[]> {
    return this.http.get<Recovery[]>(
      `${this.baseUrl}/recovery`
    );
  }

  updateRecoveryStatus(
    recoveryId: number,
    request: UpdateRecoveryStatusRequest
  ): Observable<Recovery> {
    return this.http.patch<Recovery>(
      `${this.baseUrl}/recovery/${recoveryId}/status`,
      request
    );
  }
}

