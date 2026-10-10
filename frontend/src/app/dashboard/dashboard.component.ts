import { Component, OnInit } from '@angular/core';

import { Router } from '@angular/router';
import { ApiService } from '../services/api.service';

@Component({ standalone: false,
  selector: 'app-dashboard',
  
  template: `
    <div class="dashboard">
      <header>
        <h1>FlowAI Dashboard</h1>
        <div>
          <button class="primary" (click)="goToSetup()" style="margin-right:15px;">+ New Pipeline</button>
          <button class="danger" (click)="logout()">Logout</button>
        </div>
      </header>
      
      <div class="content">
        <h2>Your Data Pipelines</h2>
        <button (click)="loadPipelines()">Refresh List</button>
        
        <table *ngIf="pipelines.length > 0">
          <thead>
            <tr>
              <th>Name</th>
              <th>Status</th>
              <th>Source</th>
              <th>Destination</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let p of pipelines">
              <td>{{ p.name }}</td>
              <td>
                <span [class.active-text]="p.status === 'ACTIVE'" [class.draft-text]="p.status === 'DRAFT'">
                  {{ p.status }}
                </span>
              </td>
              <td>{{ p.sourceName }}</td>
              <td>{{ p.destinationName }}</td>
              <td>
                <button (click)="autoMap(p.id)">Auto-Map with AI</button>
                <button class="primary" (click)="triggerSync(p.id)">Run Manual Sync</button>
                <button class="warning" (click)="viewErrors(p.id)">View DLQ</button>
                <button *ngIf="p.status === 'DRAFT'" class="success" (click)="activate(p.id)">Activate Cron</button>
                <button *ngIf="p.status === 'ACTIVE'" class="danger" (click)="pause(p.id)">Pause Cron</button>
              </td>
            </tr>
          </tbody>
        </table>
        
        <div *ngIf="mappingResult" class="mapping-box">
          <h3>AI Generated Schema Mapping (Verify & Save)</h3>
          <pre>{{ mappingResult | json }}</pre>
          <button class="primary" (click)="saveMapping()">Save Mapping Config</button>
        </div>

        <div *ngIf="dlqRecords.length > 0" class="dlq-box">
          <h3>Dead Letter Queue (Failed Records)</h3>
          <button (click)="dlqRecords = []">Close Viewer</button>
          <table>
            <thead>
              <tr>
                <th>Target Table</th>
                <th>Error Message</th>
                <th>Raw Payload</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let err of dlqRecords">
                <td>{{ err.targetTable }}</td>
                <td class="error-text">{{ err.failureReason }}</td>
                <td><pre>{{ err.jsonPayload }}</pre></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard { font-family: sans-serif; padding: 20px; }
    header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #ccc; padding-bottom: 10px; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
    th { background-color: #f4f4f4; }
    button { margin-right: 5px; padding: 8px 12px; cursor: pointer; border: 1px solid #ccc; border-radius: 4px; }
    button.primary { background-color: #007bff; color: white; border: none; }
    button.success { background-color: #28a745; color: white; border: none; }
    button.danger { background-color: #dc3545; color: white; border: none; }
    button.warning { background-color: #ffc107; color: black; border: none; font-weight: bold; }
    .mapping-box { margin-top: 20px; padding: 15px; border: 2px solid #007bff; background: #f8f9fa; border-radius: 5px; }
    .dlq-box { margin-top: 20px; padding: 15px; border: 2px solid #dc3545; background: #fff3f3; border-radius: 5px; overflow-x: auto; }
    .error-text { color: #dc3545; font-weight: bold; }
    .active-text { color: #28a745; font-weight: bold; }
    .draft-text { color: gray; font-weight: bold; }
    pre { white-space: pre-wrap; word-wrap: break-word; font-size: 12px; margin: 0; }
  `]
})
export class DashboardComponent implements OnInit {
  pipelines: any[] = [];
  mappingResult: any = null;
  selectedPipelineId: string | null = null;
  dlqRecords: any[] = [];

  constructor(private api: ApiService, private router: Router) {}

  ngOnInit() { this.loadPipelines(); }

  loadPipelines() {
    this.api.getPipelines().subscribe(res => this.pipelines = res.data);
  }

  autoMap(id: string) {
    this.selectedPipelineId = id;
    this.api.autoMapPipeline(id).subscribe({
      next: (res) => this.mappingResult = res.data,
      error: () => alert('Ensure Ollama is running')
    });
  }
  
  saveMapping() {
    this.api.savePipelineConfig(this.selectedPipelineId!, JSON.stringify(this.mappingResult)).subscribe(() => {
      alert('Configuration saved!'); this.mappingResult = null;
    });
  }

  triggerSync(id: string) {
    this.api.triggerSync(id).subscribe(() => alert('Manual Sync triggered!'));
  }

  activate(id: string) {
    this.api.updatePipelineStatus(id, 'ACTIVE').subscribe(() => this.loadPipelines());
  }

  pause(id: string) {
    this.api.updatePipelineStatus(id, 'PAUSED').subscribe(() => this.loadPipelines());
  }

  viewErrors(id: string) {
    this.api.getDlqRecords(id).subscribe(res => this.dlqRecords = res.data);
  }

  goToSetup() { this.router.navigate(['/setup']); }
  logout() { localStorage.removeItem('token'); window.location.href = '/login'; }
}
