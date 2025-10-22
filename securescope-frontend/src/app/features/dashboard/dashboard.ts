import { DatePipe } from '@angular/common';
import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import type { Chart, ChartConfiguration } from 'chart.js';
import { forkJoin } from 'rxjs';
import { DashboardMetric, DashboardSummary, RecentScan } from '../../core/models/dashboard';
import { DashboardService } from '../../core/services/dashboard.service';
import { MaterialModule } from '../../shared/material/material.module';

@Component({
  selector: 'app-dashboard',
  imports: [DatePipe, MaterialModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements AfterViewInit, OnDestroy, OnInit {
  @ViewChild('severityCanvas') private severityCanvas?: ElementRef<HTMLCanvasElement>;
  @ViewChild('owaspCanvas') private owaspCanvas?: ElementRef<HTMLCanvasElement>;

  private readonly dashboardService = inject(DashboardService);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly displayedColumns = ['scanName', 'status', 'score', 'risk', 'findings', 'completedAt'];
  protected summary: DashboardSummary | null = null;
  protected severitySummary: DashboardMetric[] = [];
  protected owaspSummary: DashboardMetric[] = [];
  protected isLoading = false;

  private severityChart?: Chart;
  private owaspChart?: Chart;
  private viewReady = false;
  private chartConstructor?: typeof Chart;

  ngOnInit(): void {
    this.loadDashboard();
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
    this.renderCharts();
  }

  ngOnDestroy(): void {
    this.severityChart?.destroy();
    this.owaspChart?.destroy();
  }

  protected riskClass(scan: RecentScan): string {
    return `risk-pill risk-${scan.riskLevel.toLowerCase()}`;
  }

  private loadDashboard(): void {
    this.isLoading = true;

    forkJoin({
      summary: this.dashboardService.getSummary(),
      severitySummary: this.dashboardService.getSeveritySummary(),
      owaspSummary: this.dashboardService.getOwaspSummary()
    }).subscribe({
      next: ({ summary, severitySummary, owaspSummary }) => {
        this.summary = summary;
        this.severitySummary = severitySummary;
        this.owaspSummary = owaspSummary;
        this.isLoading = false;
        this.renderCharts();
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Unable to load dashboard metrics.', 'Close', { duration: 4000 });
      }
    });
  }

  private async renderCharts(): Promise<void> {
    if (!this.viewReady || !this.summary) {
      return;
    }

    const ChartConstructor = await this.getChartConstructor();

    this.renderSeverityChart(ChartConstructor);
    this.renderOwaspChart(ChartConstructor);
  }

  private renderSeverityChart(ChartConstructor: typeof Chart): void {
    if (!this.severityCanvas) {
      return;
    }

    this.severityChart?.destroy();

    const config: ChartConfiguration<'doughnut'> = {
      type: 'doughnut',
      data: {
        labels: this.severitySummary.map((item) => item.label),
        datasets: [
          {
            data: this.severitySummary.map((item) => item.value),
            backgroundColor: ['#94a3b8', '#38bdf8', '#facc15', '#fb923c', '#ef4444'],
            borderWidth: 0
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom'
          }
        }
      }
    };

    this.severityChart = new ChartConstructor(this.severityCanvas.nativeElement, config);
  }

  private renderOwaspChart(ChartConstructor: typeof Chart): void {
    if (!this.owaspCanvas) {
      return;
    }

    this.owaspChart?.destroy();

    const labels = this.owaspSummary.length > 0 ? this.owaspSummary.map((item) => item.label) : ['No Findings'];
    const values = this.owaspSummary.length > 0 ? this.owaspSummary.map((item) => item.value) : [1];

    const config: ChartConfiguration<'bar'> = {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Findings',
            data: values,
            backgroundColor: '#0f766e',
            borderRadius: 6
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              precision: 0
            }
          }
        },
        plugins: {
          legend: {
            display: false
          }
        }
      }
    };

    this.owaspChart = new ChartConstructor(this.owaspCanvas.nativeElement, config);
  }

  private async getChartConstructor(): Promise<typeof Chart> {
    if (!this.chartConstructor) {
      const chartModule = await import('chart.js/auto');
      this.chartConstructor = chartModule.default;
    }

    return this.chartConstructor;
  }
}
