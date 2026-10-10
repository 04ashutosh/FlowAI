import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../services/api.service';

@Component({ standalone: false,
  selector: 'app-login',
  template: `
    <div class="container">
      <h2>FlowAI Login</h2>
      <div *ngIf="error" class="error">{{ error }}</div>
      
      <input type="email" [(ngModel)]="email" placeholder="Email" />
      <input type="password" [(ngModel)]="password" placeholder="Password" />
      
      <button (click)="login()">Login</button>
      
      <hr />
      
      <h3>Register</h3>
      <input type="text" [(ngModel)]="regFullName" placeholder="Full Name" />
      <input type="email" [(ngModel)]="regEmail" placeholder="Email" />
      <input type="password" [(ngModel)]="regPassword" placeholder="Password" />
      <button (click)="register()">Register</button>
    </div>
  `,
  styles: [`
    .container { max-width: 400px; margin: 50px auto; font-family: sans-serif; }
    input, button { display: block; width: 100%; margin-bottom: 10px; padding: 10px; }
    .error { color: red; margin-bottom: 10px; font-weight: bold; }
  `]
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';
  
  regFullName = '';
  regEmail = '';
  regPassword = '';

  constructor(private api: ApiService, private router: Router) {}

  login() {
    this.api.login({ email: this.email, password: this.password }).subscribe({
      next: (res) => {
        localStorage.setItem('token', res.data.token);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => this.error = 'Login failed. Check credentials.'
    });
  }

  register() {
    this.api.register({ fullName: this.regFullName, email: this.regEmail, password: this.regPassword }).subscribe({
      next: (res) => {
        localStorage.setItem('token', res.data.token);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => this.error = 'Registration failed.'
    });
  }
}
