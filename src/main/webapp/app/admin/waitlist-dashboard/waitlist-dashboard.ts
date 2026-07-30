import { DOCUMENT, DatePipe, DecimalPipe, TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { BucketType, MetricKey, MetricSeries, MetricSummary, WaitlistSignupRow } from './dashboard.model';
import { DashboardService, saveBlob } from './dashboard.service';
import LineChart from './line-chart';

const PAGE_SIZE = 20;

/**
 * The mini admin dashboard: captured emails and the metrics derived from them.
 *
 * <p>Deliberately not one of JHipster's generated entity screens. Those are CRUD over a table; this
 * answers "how is the launch going", which is a different question with a different shape.
 */
@Component({
  selector: 'jhi-waitlist-dashboard',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './waitlist-dashboard.html',
  styleUrl: './waitlist-dashboard.scss',
  imports: [FormsModule, LineChart, DatePipe, DecimalPipe, TitleCasePipe],
})
export default class WaitlistDashboard implements OnInit {
  readonly metrics: { key: MetricKey; label: string }[] = [
    { key: 'WAITLIST_SIGNUPS', label: 'Signups' },
    { key: 'WAITLIST_CONFIRMED', label: 'Confirmed' },
    { key: 'PAGE_VIEWS', label: 'Page views' },
    { key: 'PLEDGE_CLICKS', label: 'Pledge clicks' },
    { key: 'UNIQUE_VISITORS', label: 'Unique visitors' },
    { key: 'WAITLIST_UNSUBSCRIBED', label: 'Unsubscribed' },
  ];

  readonly buckets: BucketType[] = ['HOUR', 'DAY', 'WEEK', 'MONTH', 'QUARTER', 'YEAR'];
  readonly statuses = ['', 'PENDING', 'CONFIRMED', 'UNSUBSCRIBED', 'BOUNCED'];

  // Chart controls
  metric: MetricKey = 'WAITLIST_SIGNUPS';
  bucket: BucketType = 'DAY';
  splitByCampaign = false;

  // Table controls
  statusFilter = '';
  page = 0;

  readonly summary = signal<MetricSummary | null>(null);
  readonly series = signal<MetricSeries | null>(null);
  readonly rows = signal<WaitlistSignupRow[]>([]);
  readonly totalRows = signal(0);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly pageSize = PAGE_SIZE;
  readonly totalPages = computed(() => Math.max(1, Math.ceil(this.totalRows() / PAGE_SIZE)));

  readonly chartTitle = computed(() => {
    const label = this.metrics.find(m => m.key === this.metric)?.label ?? this.metric;
    return `${label} per ${this.bucket.toLowerCase()}`;
  });

  private readonly dashboardService = inject(DashboardService);
  private readonly document = inject(DOCUMENT);

  ngOnInit(): void {
    this.reloadSummary();
    this.reloadSeries();
    this.reloadRows();
  }

  onChartControlsChanged(): void {
    this.reloadSeries();
  }

  onFilterChanged(): void {
    // Any filter change invalidates the current page number — staying on page 4 of a result set
    // that now has two pages shows an empty table and looks like a failure.
    this.page = 0;
    this.reloadRows();
  }

  goToPage(page: number): void {
    this.page = Math.min(Math.max(0, page), this.totalPages() - 1);
    this.reloadRows();
  }

  exportEmails(): void {
    this.downloadCsv(this.dashboardService.waitlistCsvUrl(this.statusFilter || null), 'waitlist.csv');
  }

  exportChart(): void {
    const dimension = this.splitByCampaign ? 'utmSource' : null;
    this.downloadCsv(
      this.dashboardService.metricsCsvUrl(this.metric, this.bucket, dimension),
      `metrics-${this.metric.toLowerCase()}-${this.bucket.toLowerCase()}.csv`,
    );
  }

  private downloadCsv(url: string, filename: string): void {
    this.dashboardService.download(url, filename).subscribe({
      next: blob => saveBlob(blob, filename, this.document),
      error: () => this.error.set('The export could not be downloaded.'),
    });
  }

  private reloadSummary(): void {
    this.dashboardService.summary().subscribe({
      next: summary => this.summary.set(summary),
      error: () => this.error.set('Could not load the summary.'),
    });
  }

  private reloadSeries(): void {
    const dimension = this.splitByCampaign ? 'utmSource' : null;
    this.dashboardService.series(this.metric, this.bucket, dimension).subscribe({
      next: series => this.series.set(series),
      error: () => this.error.set('Could not load the chart.'),
    });
  }

  private reloadRows(): void {
    this.loading.set(true);
    this.dashboardService
      .signups({
        page: this.page,
        size: PAGE_SIZE,
        sort: 'capturedAt,desc',
        // JHipster's criteria syntax — the JDL's `filter` on WaitlistSignup is what provides it.
        ...(this.statusFilter ? { 'status.equals': this.statusFilter } : {}),
      })
      .subscribe({
        next: response => {
          this.rows.set(response.body ?? []);
          this.totalRows.set(Number(response.headers.get('X-Total-Count') ?? 0));
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Could not load the captured emails.');
          this.loading.set(false);
        },
      });
  }
}
