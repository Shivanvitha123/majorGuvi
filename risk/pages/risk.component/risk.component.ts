import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject
} from '@angular/core';

import {
  CommonModule,
  CurrencyPipe,
  DatePipe
} from '@angular/common';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { Store } from '@ngrx/store';

import * as RiskActions
  from '../../store/risk.actions';

import {
  selectAssessments,
  selectSimulations,
  selectRiskError,
  selectRiskLoading,
  selectRiskSuccess
} from '../../store/risk.selectors';

@Component({
  selector: 'app-risk',

  standalone: true,

  imports: [
    CommonModule,
    ReactiveFormsModule,
    CurrencyPipe,
    DatePipe
  ],

  templateUrl: './risk.component.html',

  styleUrl: './risk.component.css',

  changeDetection:
    ChangeDetectionStrategy.OnPush
})
export class RiskComponent implements OnInit {

  private readonly store = inject(Store);

  private readonly fb = inject(FormBuilder);

  readonly assessments$ =
    this.store.select(selectAssessments);

  readonly simulations$ =
    this.store.select(selectSimulations);

  readonly loading$ =
    this.store.select(selectRiskLoading);

  readonly error$ =
    this.store.select(selectRiskError);

  readonly success$ =
    this.store.select(selectRiskSuccess);

  activeTab:
    'assessments' | 'simulations' =
    'assessments';

  showAssessmentForm = false;

  showSimulationForm = false;

  readonly assessmentForm =
    this.fb.nonNullable.group({

      businessId: [
        0,
        [
          Validators.required,
          Validators.min(1)
        ]
      ],

      policyId: [0],

      riskScore: [
        0,
        [
          Validators.required,
          Validators.min(0),
          Validators.max(100)
        ]
      ],

      riskFactors: [''],

      recommendation: ['']
    });

  readonly simulationForm =
    this.fb.nonNullable.group({

      businessId: [
        0,
        [
          Validators.required,
          Validators.min(1)
        ]
      ],

      policyId: [0],

      scenarioName: [
        '',
        Validators.required
      ],

      scenarioInput: ['']
    });

  ngOnInit(): void {

    this.store.dispatch(
      RiskActions.loadSimulations()
    );
  }

  setTab(
    tab: 'assessments' | 'simulations'
  ): void {

    this.activeTab = tab;
  }

  openAssessmentForm(): void {

    this.assessmentForm.reset({
      businessId: 0,
      policyId: 0,
      riskScore: 0,
      riskFactors: '',
      recommendation: ''
    });

    this.showAssessmentForm = true;
  }

  closeAssessmentForm(): void {

    this.showAssessmentForm = false;
  }

  createAssessment(): void {

    if (this.assessmentForm.invalid) {

      this.assessmentForm.markAllAsTouched();

      return;
    }

    const value =
      this.assessmentForm.getRawValue();

    this.store.dispatch(
      RiskActions.createAssessment({
        request: {
          businessId: value.businessId,
          policyId:
            value.policyId || null,
          riskScore: value.riskScore,
          riskFactors:
            value.riskFactors || '',
          recommendation:
            value.recommendation || ''
        }
      })
    );

    this.showAssessmentForm = false;
  }

  loadBusinessAssessments(
    businessId: number
  ): void {

    if (!businessId) {
      return;
    }

    this.store.dispatch(
      RiskActions.loadAssessments({
        businessId
      })
    );
  }

  openSimulationForm(): void {

    this.simulationForm.reset({
      businessId: 0,
      policyId: 0,
      scenarioName: '',
      scenarioInput: ''
    });

    this.showSimulationForm = true;
  }

  closeSimulationForm(): void {

    this.showSimulationForm = false;
  }

  createSimulation(): void {

    if (this.simulationForm.invalid) {

      this.simulationForm.markAllAsTouched();

      return;
    }

    const value =
      this.simulationForm.getRawValue();

    this.store.dispatch(
      RiskActions.createSimulation({
        request: {
          businessId: value.businessId,
          policyId:
            value.policyId || null,
          scenarioName:
            value.scenarioName,
          scenarioInput:
            value.scenarioInput || ''
        }
      })
    );

    this.showSimulationForm = false;
  }

  riskClass(
    level: string | null
  ): string {

    return level
      ? level.toLowerCase()
      : 'unknown';
  }
}


