import { ChangeDetectionStrategy, Component, DOCUMENT, OnInit, Renderer2, RendererFactory2, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import type { ActivatedRouteSnapshot } from '@angular/router';
import { filter } from 'rxjs/operators';

import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import dayjs from 'dayjs/esm';

import { AppPageTitleStrategy } from 'app/app-page-title-strategy';
import { AccountService } from 'app/core/auth/account.service';
import Footer from '../footer/footer';
import PageRibbon from '../profiles/page-ribbon';

@Component({
  selector: 'jhi-main',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './main.html',
  providers: [AppPageTitleStrategy],
  imports: [RouterOutlet, Footer, PageRibbon],
})
export default class Main implements OnInit {
  /**
   * Whether to render the admin chrome. Defaults to true so that any route which does not say
   * otherwise keeps the JHipster shell — a new admin screen should not have to opt in.
   */
  readonly showChrome = signal(true);

  private readonly renderer: Renderer2;
  private readonly htmlElement: HTMLElement;

  private readonly router = inject(Router);
  private readonly appPageTitleStrategy = inject(AppPageTitleStrategy);
  private readonly accountService = inject(AccountService);
  private readonly document = inject(DOCUMENT);
  private readonly translateService = inject(TranslateService);
  private readonly rootRenderer = inject(RendererFactory2);

  constructor() {
    this.htmlElement = this.document.documentElement;
    this.renderer = this.rootRenderer.createRenderer(this.htmlElement, null);
  }

  ngOnInit(): void {
    // try to log in automatically
    this.accountService.identity().subscribe();

    this.updateChrome();
    this.router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe(() => this.updateChrome());

    this.translateService.onLangChange.subscribe((langChangeEvent: LangChangeEvent) => {
      this.appPageTitleStrategy.updateTitle(this.router.routerState.snapshot);
      dayjs.locale(langChangeEvent.lang);
      this.renderer.setAttribute(this.htmlElement, 'lang', langChangeEvent.lang);
    });
  }

  /**
   * Walks to the deepest activated route and reads its `chrome` flag.
   *
   * <p>The deepest route wins because `data` is inherited down the tree: reading it off the root
   * gives whatever the top-level route declared, which for a lazy-loaded child is nothing useful.
   *
   * <p>Read off the router's own snapshot rather than by injecting ActivatedRoute. Injecting it
   * drags `PlatformLocation` into this component's dependency graph, and the generated main.spec
   * then fails to instantiate at all — "The injectable 'PlatformLocation' needs to be compiled
   * using the JIT compiler". The Router is already here for the title strategy and carries the
   * same tree.
   */
  private updateChrome(): void {
    let route: ActivatedRouteSnapshot | null = this.router.routerState.snapshot.root;
    while (route.firstChild) {
      route = route.firstChild;
    }
    this.showChrome.set(route.data.chrome !== false);
  }
}
