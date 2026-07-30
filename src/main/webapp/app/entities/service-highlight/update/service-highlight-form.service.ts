import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IServiceHighlight, NewServiceHighlight } from '../service-highlight.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IServiceHighlight for edit and NewServiceHighlightFormGroupInput for create.
 */
type ServiceHighlightFormGroupInput = IServiceHighlight | PartialWithRequiredKeyOf<NewServiceHighlight>;

type ServiceHighlightFormDefaults = Pick<NewServiceHighlight, 'id'>;

type ServiceHighlightFormGroupContent = {
  id: FormControl<IServiceHighlight['id'] | NewServiceHighlight['id']>;
  label: FormControl<IServiceHighlight['label']>;
  displayOrder: FormControl<IServiceHighlight['displayOrder']>;
  service: FormControl<IServiceHighlight['service']>;
};

export type ServiceHighlightFormGroup = FormGroup<ServiceHighlightFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ServiceHighlightFormService {
  createServiceHighlightFormGroup(serviceHighlight?: ServiceHighlightFormGroupInput): ServiceHighlightFormGroup {
    const serviceHighlightRawValue = {
      ...this.getFormDefaults(),
      ...(serviceHighlight ?? { id: null }),
    };
    return new FormGroup<ServiceHighlightFormGroupContent>({
      id: new FormControl(
        { value: serviceHighlightRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      label: new FormControl(serviceHighlightRawValue.label, {
        validators: [Validators.required, Validators.maxLength(160)],
      }),
      displayOrder: new FormControl(serviceHighlightRawValue.displayOrder, {
        validators: [Validators.required, Validators.min(0)],
      }),
      service: new FormControl(serviceHighlightRawValue.service, {
        validators: [Validators.required],
      }),
    });
  }

  getServiceHighlight(form: ServiceHighlightFormGroup): IServiceHighlight | NewServiceHighlight {
    return form.getRawValue();
  }

  resetForm(form: ServiceHighlightFormGroup, serviceHighlight: ServiceHighlightFormGroupInput): void {
    const serviceHighlightRawValue = { ...this.getFormDefaults(), ...serviceHighlight };
    form.reset({
      ...serviceHighlightRawValue,
      id: { value: serviceHighlightRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ServiceHighlightFormDefaults {
    return {
      id: null,
    };
  }
}
