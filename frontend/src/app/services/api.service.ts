import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({ 'Authorization': `Bearer ${token}` });
  }

  login(data: any): Observable<any> { return this.http.post(`${this.baseUrl}/auth/login`, data); }
  register(data: any): Observable<any> { return this.http.post(`${this.baseUrl}/auth/register`, data); }

  getSources(): Observable<any> { return this.http.get(`${this.baseUrl}/sources`, { headers: this.getHeaders() }); }
  createSource(data: any): Observable<any> { return this.http.post(`${this.baseUrl}/sources`, data, { headers: this.getHeaders() }); }

  getDestinations(): Observable<any> { return this.http.get(`${this.baseUrl}/destinations`, { headers: this.getHeaders() }); }
  createDestination(data: any): Observable<any> { return this.http.post(`${this.baseUrl}/destinations`, data, { headers: this.getHeaders() }); }

  getPipelines(): Observable<any> { return this.http.get(`${this.baseUrl}/pipelines`, { headers: this.getHeaders() }); }
  createPipeline(data: any): Observable<any> { return this.http.post(`${this.baseUrl}/pipelines`, data, { headers: this.getHeaders() }); }
  
  updatePipelineStatus(pipelineId: string, status: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/pipelines/${pipelineId}/status?status=${status}`, {}, { headers: this.getHeaders() });
  }

  autoMapPipeline(pipelineId: string): Observable<any> { return this.http.post(`${this.baseUrl}/intelligence/pipelines/${pipelineId}/auto-map`, {}, { headers: this.getHeaders() }); }
  savePipelineConfig(pipelineId: string, mappingJson: string): Observable<any> { return this.http.post(`${this.baseUrl}/pipelines/${pipelineId}/config`, { mappingJson }, { headers: this.getHeaders() }); }
  triggerSync(pipelineId: string): Observable<any> { return this.http.post(`${this.baseUrl}/sync/${pipelineId}/trigger`, {}, { headers: this.getHeaders() }); }
  getDlqRecords(pipelineId: string): Observable<any> { return this.http.get(`${this.baseUrl}/dlq/pipeline/${pipelineId}`, { headers: this.getHeaders() }); }
}