import { Component, OnInit } from '@angular/core';
import { ApiService } from '../services/api.service';

@Component({
  selector: 'app-dashboard',
  template: `
    <div class="dashboard">
      <header>
        <h1>FlowAI Dashboard</h1>
        <button class="danger" (click)="logout()">Logout</button>
      </header>
      
      <div class="content">
        <h2>Your Active Data Pipelines</h2>
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
              <td>{{ p.status }}</td>
              <td>{{ p.sourceName }}</td>
              <td>{{ p.destinationName }}</td>
              <td>
                <button (click)="autoMap(p.id)">Auto-Map with AI</button>
                <button class="primary" (click)="triggerSync(p.id)">Run Sync</button>
                <button class="warning" (click)="viewErrors(p.id)">View Errors (DLQ)</button>
              </td>
            </tr>
          </tbody>
        </table>
        <p *ngIf="pipelines.length === 0">No pipelines found. Please create via API for now.</p>
        
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
                <th>Time</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let err of dlqRecords">
                <td>{{ err.targetTable }}</td>
                <td class="error-text">{{ err.failureReason }}</td>
                <td><pre>{{ err.jsonPayload }}</pre></td>
                <td>{{ err.timeCreated | date:'short' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <p *ngIf="showDlqEmptyMsg" class="success-text">No errors found in the DLQ for this pipeline! All syncs were perfect.</p>

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
    button.danger { background-color: #dc3545; color: white; border: none; }
    button.warning { background-color: #ffc107; color: black; border: none; font-weight: bold; }
    .mapping-box { margin-top: 20px; padding: 15px; border: 2px solid #007bff; background: #f8f9fa; border-radius: 5px; }
    .dlq-box { margin-top: 20px; padding: 15px; border: 2px solid #dc3545; background: #fff3f3; border-radius: 5px; overflow-x: auto; }
    .error-text { color: #dc3545; font-weight: bold; max-width: 300px; }
    .success-text { color: #28a745; font-weight: bold; margin-top: 20px; }
    pre { white-space: pre-wrap; word-wrap: break-word; font-size: 12px; margin: 0; }
  `]
})
export class DashboardComponent implements OnInit {
  pipelines: any[] = [];
  mappingResult: any = null;
  selectedPipelineId: string | null = null;
  dlqRecords: any[] = [];
  showDlqEmptyMsg = false;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadPipelines();
  }

  loadPipelines() {
    this.api.getPipelines().subscribe({
      next: (res) => this.pipelines = res.data,
      error: (err) => console.error(err)
    });
  }

  autoMap(id: string) {
    this.selectedPipelineId = id;
    this.mappingResult = null;
    this.dlqRecords = [];
    this.showDlqEmptyMsg = false;
    alert('Asking Local Ollama AI for mapping... this may take up to a minute depending on your hardware.');
    this.api.autoMapPipeline(id).subscribe({
      next: (res) => this.mappingResult = res.data,
      error: (err) => alert('AI mapping failed! Ensure Ollama is running.')
    });
  }
  
  saveMapping() {
    if (this.selectedPipelineId && this.mappingResult) {
      this.api.savePipelineConfig(this.selectedPipelineId, JSON.stringify(this.mappingResult)).subscribe({
        next: () => {
          alert('Configuration saved to database!');
          this.mappingResult = null;
        },
        error: () => alert('Save failed')
      });
    }
  }

  triggerSync(id: string) {
    this.api.triggerSync(id).subscribe({
      next: () => alert('Sync triggered successfully! Sent to Kafka Pipeline.'),
      error: () => alert('Sync failed')
    });
  }

  viewErrors(id: string) {
    this.mappingResult = null;
    this.showDlqEmptyMsg = false;
    this.api.getDlqRecords(id).subscribe({
      next: (res) => {
        this.dlqRecords = res.data;
        if (this.dlqRecords.length === 0) {
          this.showDlqEmptyMsg = true;
          setTimeout(() => this.showDlqEmptyMsg = false, 5000);
        }
      },
      error: () => alert('Failed to fetch DLQ records')
    });
  }

  logout() {
    localStorage.removeItem('token');
    window.location.href = '/login';
  }
}