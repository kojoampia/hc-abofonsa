import { DatePipe, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input, signal } from '@angular/core';

import { BucketType, MetricSeries } from './dashboard.model';

interface PlottedSeries {
  label: string;
  color: string;
  total: number;
  path: string;
  markers: { x: number; y: number; value: number }[];
  labelX: number;
  labelY: number;
}

/**
 * A multi-series line chart, drawn as inline SVG.
 *
 * <p>No charting library. The dependency would be larger than this file, and the one chart this
 * dashboard needs is a line over time — which is a path command and a linear scale.
 *
 * <p>Colours are the validated categorical slots from the design system's palette, assigned in
 * fixed order and never cycled: a series keeps its colour when a filter removes its neighbours, so
 * the chart does not repaint itself as the data narrows.
 */
@Component({
  selector: 'jhi-line-chart',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [DatePipe, DecimalPipe],
  styleUrl: './line-chart.scss',
  templateUrl: './line-chart.html',
})
export default class LineChart {
  /**
   * Validated against the light chart surface: lightness band, chroma floor, CVD separation
   * (worst adjacent pair ΔE 9.1 protan) and normal-vision floor all pass. Three of the five sit
   * under 3:1 contrast, which obliges visible labels — hence the legend, the direct labels and the
   * table below the chart, none of which are optional.
   */
  private static readonly SLOTS = ['#2a78d6', '#eb6834', '#1baf7a', '#eda100', '#e87ba4'];

  /** A single series is the brand navy: alone, it needs contrast, not categorical separation. */
  private static readonly SINGLE = '#0d3058';

  readonly data = input.required<MetricSeries>();
  readonly title = input<string>('');

  // 4:1. Wide enough that a month of daily points is legible and short enough that the chart
  // and the table it belongs with fit on one screen.
  readonly width = 1200;
  readonly height = 300;
  readonly padding = { top: 16, right: 96, bottom: 34, left: 52 };

  readonly hoverIndex = signal<number | null>(null);

  readonly plotWidth = computed(() => this.width - this.padding.left - this.padding.right);
  readonly plotHeight = computed(() => this.height - this.padding.top - this.padding.bottom);

  /** Buckets on the x-axis. Every series shares them, because the API gap-fills each one. */
  readonly buckets = computed(() => this.data().series[0]?.points ?? []);

  readonly maxValue = computed(() => {
    const max = Math.max(0, ...this.data().series.flatMap(s => s.points.map(p => p.value)));
    // A flat-zero series still needs a scale, or every point divides by zero and lands at NaN.
    return max === 0 ? 1 : max;
  });

  /** Four gridlines, on rounded values rather than exact fractions of the max. */
  readonly yTicks = computed(() => {
    const max = this.maxValue();
    const step = niceStep(max / 4);
    const ticks: { value: number; y: number }[] = [];
    for (let value = 0; value <= max + step / 2; value += step) {
      ticks.push({ value, y: this.yFor(value) });
    }
    return ticks;
  });

  /** At most eight x labels, however many buckets there are — more than that overlap and stop being readable. */
  readonly xTicks = computed(() => {
    const points = this.buckets();
    if (points.length === 0) {
      return [];
    }
    const stride = Math.max(1, Math.ceil(points.length / 8));
    return points
      .map((point, index) => ({ index, bucketStart: point.bucketStart }))
      .filter(tick => tick.index % stride === 0)
      .map(tick => ({ ...tick, x: this.xFor(tick.index) }));
  });

  readonly plotted = computed<PlottedSeries[]>(() => {
    const all = this.data().series;
    return (
      all
        .map((series, seriesIndex) => {
          const markers = series.points.map((point, index) => ({
            x: this.xFor(index),
            y: this.yFor(point.value),
            value: point.value,
          }));
          const path = markers.map((marker, index) => `${index === 0 ? 'M' : 'L'}${marker.x.toFixed(1)},${marker.y.toFixed(1)}`).join(' ');
          const last = markers.length > 0 ? markers[markers.length - 1] : undefined;
          return {
            label: series.label,
            color: all.length === 1 ? LineChart.SINGLE : LineChart.SLOTS[seriesIndex % LineChart.SLOTS.length],
            total: series.total,
            path,
            markers,
            labelX: (last?.x ?? 0) + 8,
            labelY: last?.y ?? 0,
          };
        })
        // Two series ending on the same value put their labels in the same place, and the result is
        // unreadable overprinted text — "(none)" and "newsletter" rendered as "(nond)etter". Nudge
        // them apart vertically; the leader is still unambiguous because the label sits at the end of
        // its own line.
        .map(spreadLabels(all.length))
    );
  });

  /** Direct labels only up to four series; beyond that they collide and the legend carries identity. */
  readonly showDirectLabels = computed(() => this.plotted().length > 1 && this.plotted().length <= 4);

  readonly showLegend = computed(() => this.plotted().length > 1);

  readonly hovered = computed(() => {
    const index = this.hoverIndex();
    if (index === null) {
      return null;
    }
    // No null-check on the bucket: hoverIndex is always clamped to this array's bounds in onMove,
    // so TypeScript is right that a guard here can never fire.
    const bucket = this.buckets()[index];
    return {
      bucketStart: bucket.bucketStart,
      x: this.xFor(index),
      rows: this.plotted().map(series => ({
        label: series.label,
        color: series.color,
        value: series.markers[index]?.value ?? 0,
      })),
    };
  });

  /** Date format per zoom level — an hourly chart labelled by date is unreadable, and vice versa. */
  readonly tickFormat = computed(() => formatFor(this.data().bucketType));

  onMove(event: MouseEvent, svg: Element): void {
    const count = this.buckets().length;
    if (count === 0) {
      return;
    }
    const rect = svg.getBoundingClientRect();
    // The SVG scales to its container, so client pixels must be mapped back through the viewBox
    // before they mean anything in plot coordinates.
    const scaled = ((event.clientX - rect.left) / rect.width) * this.width;
    const ratio = (scaled - this.padding.left) / this.plotWidth();
    const index = Math.round(ratio * Math.max(1, count - 1));
    this.hoverIndex.set(Math.min(count - 1, Math.max(0, index)));
  }

  onLeave(): void {
    this.hoverIndex.set(null);
  }

  xFor(index: number): number {
    const count = this.buckets().length;
    if (count <= 1) {
      return this.padding.left + this.plotWidth() / 2;
    }
    return this.padding.left + (index / (count - 1)) * this.plotWidth();
  }

  yFor(value: number): number {
    return this.padding.top + this.plotHeight() - (value / this.maxValue()) * this.plotHeight();
  }
}

/**
 * Pushes overlapping end-of-line labels apart.
 *
 * <p>Applied as a map over the already-built list so each series keeps its own colour and order —
 * only the label's y moves, never the line's.
 */
function spreadLabels(count: number): (series: PlottedSeries, index: number, all: PlottedSeries[]) => PlottedSeries {
  const MIN_GAP = 14;
  return (series, index, all) => {
    if (count < 2) {
      return series;
    }
    // Walk in drawing order and keep each label at least MIN_GAP from every earlier one.
    let y = series.labelY;
    for (let i = 0; i < index; i++) {
      const other = all[i].labelY;
      if (Math.abs(y - other) < MIN_GAP) {
        y = other + MIN_GAP;
      }
    }
    all[index] = { ...series, labelY: y };
    return all[index];
  };
}

/** Rounds a raw step up to something a person would choose: 1, 2, 5, 10, 20, 50… */
function niceStep(raw: number): number {
  if (raw <= 0) {
    return 1;
  }
  const magnitude = Math.pow(10, Math.floor(Math.log10(raw)));
  const normalised = raw / magnitude;
  const nice = normalised <= 1 ? 1 : normalised <= 2 ? 2 : normalised <= 5 ? 5 : 10;
  return Math.max(1, nice * magnitude);
}

function formatFor(bucketType: BucketType): string {
  switch (bucketType) {
    case 'HOUR':
      return 'd MMM HH:mm';
    case 'DAY':
    case 'WEEK':
      return 'd MMM';
    case 'MONTH':
    case 'QUARTER':
      return 'MMM yyyy';
    default:
      return 'yyyy';
  }
}
