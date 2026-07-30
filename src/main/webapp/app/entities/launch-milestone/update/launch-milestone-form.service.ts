import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILaunchMilestone, NewLaunchMilestone } from '../launch-milestone.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILaunchMilestone for edit and NewLaunchMilestoneFormGroupInput for create.
 */
type LaunchMilestoneFormGroupInput = ILaunchMilestone | PartialWithRequiredKeyOf<NewLaunchMilestone>;

type LaunchMilestoneFormDefaults = Pick<NewLaunchMilestone, 'id' | 'current' | 'published'>;

type LaunchMilestoneFormGroupContent = {
  id: FormControl<ILaunchMilestone['id'] | NewLaunchMilestone['id']>;
  phaseLabel: FormControl<ILaunchMilestone['phaseLabel']>;
  title: FormControl<ILaunchMilestone['title']>;
  body: FormControl<ILaunchMilestone['body']>;
  milestoneDate: FormControl<ILaunchMilestone['milestoneDate']>;
  current: FormControl<ILaunchMilestone['current']>;
  displayOrder: FormControl<ILaunchMilestone['displayOrder']>;
  published: FormControl<ILaunchMilestone['published']>;
};

export type LaunchMilestoneFormGroup = FormGroup<LaunchMilestoneFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LaunchMilestoneFormService {
  createLaunchMilestoneFormGroup(launchMilestone?: LaunchMilestoneFormGroupInput): LaunchMilestoneFormGroup {
    const launchMilestoneRawValue = {
      ...this.getFormDefaults(),
      ...(launchMilestone ?? { id: null }),
    };
    return new FormGroup<LaunchMilestoneFormGroupContent>({
      id: new FormControl(
        { value: launchMilestoneRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      phaseLabel: new FormControl(launchMilestoneRawValue.phaseLabel, {
        validators: [Validators.required, Validators.maxLength(40)],
      }),
      title: new FormControl(launchMilestoneRawValue.title, {
        validators: [Validators.required, Validators.maxLength(120)],
      }),
      body: new FormControl(launchMilestoneRawValue.body),
      milestoneDate: new FormControl(launchMilestoneRawValue.milestoneDate),
      current: new FormControl(launchMilestoneRawValue.current, {
        validators: [Validators.required],
      }),
      displayOrder: new FormControl(launchMilestoneRawValue.displayOrder, {
        validators: [Validators.required, Validators.min(0)],
      }),
      published: new FormControl(launchMilestoneRawValue.published, {
        validators: [Validators.required],
      }),
    });
  }

  getLaunchMilestone(form: LaunchMilestoneFormGroup): ILaunchMilestone | NewLaunchMilestone {
    return form.getRawValue();
  }

  resetForm(form: LaunchMilestoneFormGroup, launchMilestone: LaunchMilestoneFormGroupInput): void {
    const launchMilestoneRawValue = { ...this.getFormDefaults(), ...launchMilestone };
    form.reset({
      ...launchMilestoneRawValue,
      id: { value: launchMilestoneRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LaunchMilestoneFormDefaults {
    return {
      id: null,
      current: false,
      published: false,
    };
  }
}
