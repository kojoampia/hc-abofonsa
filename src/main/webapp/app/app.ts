import { registerLocaleData } from '@angular/common';
import locale from '@angular/common/locales/en';
import { ChangeDetectionStrategy, Component, effect, inject } from '@angular/core';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { NgbDatepickerConfig } from '@ng-bootstrap/ng-bootstrap/datepicker';
import dayjs from 'dayjs/esm';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { TelemetryService } from 'app/core/telemetry/telemetry.service';
import { LaunchService } from 'app/launch/launch.service';

import { fontAwesomeIcons } from './config/font-awesome-icons';
import Main from './layouts/main/main';

@Component({
  selector: 'jhi-app',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: '<jhi-main />',
  imports: [Main],
})
export default class App {
  private readonly applicationConfigService = inject(ApplicationConfigService);
  private readonly iconLibrary = inject(FaIconLibrary);
  private readonly dpConfig = inject(NgbDatepickerConfig);
  private readonly telemetryService = inject(TelemetryService);
  private readonly launchService = inject(LaunchService);

  constructor() {
    this.applicationConfigService.setEndpointPrefix(SERVER_API_URL);
    registerLocaleData(locale);
    this.iconLibrary.addIcons(...fontAwesomeIcons);
    this.dpConfig.minDate = { year: dayjs().subtract(100, 'year').year(), month: 1, day: 1 };

    // Start RUM once the server has said whether to, and at what rate. Here rather than in the
    // launch page so that the admin screens are instrumented too — they make the API calls worth
    // watching, even though the public page is where the visitors are.
    //
    // `start()` is idempotent and makes its sampling decision once, so re-running this effect when
    // the content resource settles cannot produce a half-traced visit.
    effect(() => this.telemetryService.start(this.launchService.content()?.telemetry));
  }
}
