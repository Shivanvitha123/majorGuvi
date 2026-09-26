import { Injectable, inject } from '@angular/core';

import {
  Actions,
  createEffect,
  ofType
} from '@ngrx/effects';

import {
  catchError,
  map,
  mergeMap,
  of
} from 'rxjs';

import * as RiskActions from './risk.actions';

import { RiskApiService } from '../services/risk-api.service';

@Injectable()
export class RiskEffects {

  private readonly actions$ = inject(Actions);

  private readonly api = inject(RiskApiService);

  loadAssessments$ = createEffect(() =>
    this.actions$.pipe(

      ofType(
        RiskActions.loadAssessments
      ),

      mergeMap(({ businessId }) =>

        this.api
          .getByBusiness(businessId)

          .pipe(

            map(assessments =>
              RiskActions.loadAssessmentsSuccess({
                assessments
              })
            ),

            catchError(error =>
              of(
                RiskActions.riskFailure({
                  error:
                    error?.error?.message ??
                    'Failed to load risk assessments.'
                })
              )
            )
          )
      )
    )
  );

  createAssessment$ = createEffect(() =>
    this.actions$.pipe(

      ofType(
        RiskActions.createAssessment
      ),

      mergeMap(({ request }) =>

        this.api
          .createAssessment(request)

          .pipe(

            map(assessment =>
              RiskActions.createAssessmentSuccess({
                assessment
              })
            ),

            catchError(error =>
              of(
                RiskActions.riskFailure({
                  error:
                    error?.error?.message ??
                    'Failed to create risk assessment.'
                })
              )
            )
          )
      )
    )
  );

  loadSimulations$ = createEffect(() =>
    this.actions$.pipe(

      ofType(
        RiskActions.loadSimulations
      ),

      mergeMap(() =>

        this.api
          .getSimulations()

          .pipe(

            map(simulations =>
              RiskActions.loadSimulationsSuccess({
                simulations
              })
            ),

            catchError(error =>
              of(
                RiskActions.riskFailure({
                  error:
                    error?.error?.message ??
                    'Failed to load simulations.'
                })
              )
            )
          )
      )
    )
  );

  createSimulation$ = createEffect(() =>
    this.actions$.pipe(

      ofType(
        RiskActions.createSimulation
      ),

      mergeMap(({ request }) =>

        this.api
          .createSimulation(request)

          .pipe(

            map(simulation =>
              RiskActions.createSimulationSuccess({
                simulation
              })
            ),

            catchError(error =>
              of(
                RiskActions.riskFailure({
                  error:
                    error?.error?.message ??
                    'Failed to create simulation.'
                })
              )
            )
          )
      )
    )
  );
}
