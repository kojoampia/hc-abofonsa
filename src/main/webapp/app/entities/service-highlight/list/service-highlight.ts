import { ChangeDetectionStrategy, Component, OnInit, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslateModule } from '@ngx-translate/core';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { ServiceHighlightDeleteDialog } from '../delete/service-highlight-delete-dialog';
import { ServiceHighlightService } from '../service/service-highlight.service';
import { IServiceHighlight } from '../service-highlight.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-service-highlight',
  templateUrl: './service-highlight.html',
  imports: [
    RouterLink,
    FormsModule,
    FontAwesomeModule,
    AlertError,
    Alert,
    SortDirective,
    SortByDirective,
    TranslateDirective,
    TranslateModule,
  ],
})
export class ServiceHighlight implements OnInit {
  subscription: Subscription | null = null;
  readonly serviceHighlights = signal<IServiceHighlight[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly serviceHighlightService = inject(ServiceHighlightService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.serviceHighlightService.serviceHighlightsResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.serviceHighlights.set(this.fillComponentAttributesFromResponseBody([...this.serviceHighlightService.serviceHighlights()]));
    });
  }

  trackId = (item: IServiceHighlight): number => this.serviceHighlightService.getServiceHighlightIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.serviceHighlights().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(serviceHighlight: IServiceHighlight): void {
    const modalRef = this.modalService.open(ServiceHighlightDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.serviceHighlight = serviceHighlight;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend();
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected refineData(data: IServiceHighlight[]): IServiceHighlight[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IServiceHighlight[]): IServiceHighlight[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.serviceHighlightService.serviceHighlightsParams.set(queryObject);
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
