import { createAction, props } from '@ngrx/store';

import {
  RiskAssessment,
  RiskAssessmentRequest,
  Simulation,
  SimulationRequest
} from '../models/risk.models';

export const loadAssessments = createAction(
  '[Risk] Load Assessments',
  props<{ businessId: number }>()
);

export const loadAssessmentsSuccess = createAction(
  '[Risk] Load Assessments Success',
  props<{ assessments: RiskAssessment[] }>()
);

export const createAssessment = createAction(
  '[Risk] Create Assessment',
  props<{ request: RiskAssessmentRequest }>()
);

export const createAssessmentSuccess = createAction(
  '[Risk] Create Assessment Success',
  props<{ assessment: RiskAssessment }>()
);

export const loadSimulations = createAction(
  '[Risk] Load Simulations'
);

export const loadSimulationsSuccess = createAction(
  '[Risk] Load Simulations Success',
  props<{ simulations: Simulation[] }>()
);

export const createSimulation = createAction(
  '[Risk] Create Simulation',
  props<{ request: SimulationRequest }>()
);

export const createSimulationSuccess = createAction(
  '[Risk] Create Simulation Success',
  props<{ simulation: Simulation }>()
);

export const riskFailure = createAction(
  '[Risk] Failure',
  props<{ error: string }>()
);

export const clearRiskMessages = createAction(
  '[Risk] Clear Messages'
);


