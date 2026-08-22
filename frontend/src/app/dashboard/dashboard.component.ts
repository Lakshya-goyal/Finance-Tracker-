import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DashboardService } from '../services/dashboard.service';
import { AuthService } from '../services/auth.service';
import { DashboardData } from '../models/dashboard.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  dashboardData: DashboardData | null = null;
  loading: boolean = true;
  errorMessage: string = '';

  constructor(
    private dashboardService: DashboardService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.errorMessage = 'User not logged in';
      this.loading = false;
      return;
    }

    this.loading = true;
    this.dashboardService.getDashboardData(user.id).subscribe({
      next: (data) => {
        this.dashboardData = data;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Failed to load dashboard data. Please try again later.';
      }
    });
  }
}
