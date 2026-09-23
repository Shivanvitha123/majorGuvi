import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environments';
import { Business, BusinessRequest, BusinessSummary } from '../model/business.model';


@Injectable({
  providedIn: 'root'
})
export class BusinessApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/business`;

  create(request: BusinessRequest): Observable<Business> {
    return this.http.post<Business>(
      this.baseUrl,
      request
    );
  }

  getMyBusinesses(): Observable<Business[]> {
    return this.http.get<Business[]>(
      `${this.baseUrl}/me`
    );
  }

  getById(id: number): Observable<Business> {
    return this.http.get<Business>(
      `${this.baseUrl}/${id}`
    );
  }

  update(
    id: number,
    request: Partial<BusinessRequest>
  ): Observable<Business> {
    return this.http.put<Business>(
      `${this.baseUrl}/${id}`,
      request
    );
  }

  getAll(): Observable<BusinessSummary[]> {
    return this.http.get<BusinessSummary[]>(
      this.baseUrl
    );
  }
}

