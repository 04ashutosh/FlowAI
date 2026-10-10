import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../services/api.service';

@Component({ standalone: false,
  selector: 'app-setup',
  template: `
    <div class="container">
      <header>
        <h1>Pipeline Builder</h1>
        <button (click)="goToDashboard()">Back to Dashboard</button>
      </header>
      
      <div class="grid">
        <div class="card">
          <h3>1. Create Source Database</h3>
          <input [(ngModel)]="srcName" placeholder="Name" />
          <input [(ngModel)]="srcHost" placeholder="Host (e.g., localhost)" />
          <input type="number" [(ngModel)]="srcPort" placeholder="Port (e.g., 5436)" />
          <input [(ngModel)]="srcDb" placeholder="Database Name" />
          <input [(ngModel)]="srcUser" placeholder="Username" />
          <input type="password" [(ngModel)]="srcPass" placeholder="Password" />
          <button class="primary" (click)="createSource()">Save Source</button>
        </div>

        <div class="card">
          <h3>2. Create Destination Database</h3>
          <input [(ngModel)]="destName" placeholder="Name" />
          <input [(ngModel)]="destHost" placeholder="Host (e.g., localhost)" />
          <input type="number" [(ngModel)]="destPort" placeholder="Port (e.g., 5436)" />
          <input [(ngModel)]="destDb" placeholder="Database Name" />
          <input [(ngModel)]="destUser" placeholder="Username" />
          <input type="password" [(ngModel)]="destPass" placeholder="Password" />
          <button class="primary" (click)="createDestination()">Save Destination</button>
        </div>
      </div>

      <div class="card full-width" style="margin-top:20px;">
        <h3>3. Link Pipeline</h3>
        <select [(ngModel)]="selectedSrcId">
          <option [value]="null" disabled>Select Source</option>
          <option *ngFor="let s of sources" [value]="s.id">{{ s.name }}</option>
        </select>
        
        <select [(ngModel)]="selectedDestId">
          <option [value]="null" disabled>Select Destination</option>
          <option *ngFor="let d of destinations" [value]="d.id">{{ d.name }}</option>
        </select>
        
        <input [(ngModel)]="pipeName" placeholder="Pipeline Name" />
        <button class="success" (click)="createPipeline()">Create Pipeline</button>
      </div>
    </div>
  `,
  styles: [`
    .container { max-width: 800px; margin: 20px auto; font-family: sans-serif; }
    header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #ccc; padding-bottom: 10px; margin-bottom: 20px;}
    .grid { display: flex; gap: 20px; }
    .card { flex: 1; padding: 15px; border: 1px solid #ddd; border-radius: 5px; background: #f9f9f9; }
    input, select, button { display: block; width: 100%; margin-bottom: 10px; padding: 8px; box-sizing: border-box; }
    button { cursor: pointer; border: none; border-radius: 4px; font-weight: bold; }
    button.primary { background-color: #007bff; color: white; }
    button.success { background-color: #28a745; color: white; padding: 12px; }
  `]
})
export class SetupComponent implements OnInit {
  srcName = ''; srcHost = 'localhost'; srcPort = 5436; srcDb = 'source_db'; srcUser = 'source_user'; srcPass = 'source_pass';
  destName = ''; destHost = 'localhost'; destPort = 5436; destDb = 'source_db'; destUser = 'source_user'; destPass = 'source_pass';
  pipeName = ''; selectedSrcId: string | null = null; selectedDestId: string | null = null;
  
  sources: any[] = [];
  destinations: any[] = [];

  constructor(private api: ApiService, private router: Router) {}

  ngOnInit() { this.loadDropdowns(); }

  loadDropdowns() {
    this.api.getSources().subscribe(res => this.sources = res.data);
    this.api.getDestinations().subscribe(res => this.destinations = res.data);
  }

  createSource() {
    const payload = { name: this.srcName, type: 'POSTGRESQL', host: this.srcHost, port: this.srcPort, databaseName: this.srcDb, username: this.srcUser, password: this.srcPass };
    this.api.createSource(payload).subscribe(() => { alert('Source Created'); this.loadDropdowns(); });
  }

  createDestination() {
    const payload = { name: this.destName, type: 'POSTGRESQL', host: this.destHost, port: this.destPort, databaseName: this.destDb, username: this.destUser, password: this.destPass };
    this.api.createDestination(payload).subscribe(() => { alert('Destination Created'); this.loadDropdowns(); });
  }

  createPipeline() {
    if(!this.selectedSrcId || !this.selectedDestId || !this.pipeName) return alert("Fill all fields");
    const payload = { name: this.pipeName, description: 'Created via UI', sourceId: this.selectedSrcId, destinationId: this.selectedDestId };
    this.api.createPipeline(payload).subscribe(() => { 
      alert('Pipeline Created! Returning to Dashboard so you can map it.'); 
      this.router.navigate(['/dashboard']); 
    });
  }

  goToDashboard() { this.router.navigate(['/dashboard']); }
}
