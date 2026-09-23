import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';

import {
  Actions,
  createEffect,
  ofType
} from '@ngrx/effects';

import {
  catchError,
  map,
  of,
  switchMap,
  tap
} from 'rxjs';

import * as ActionsList from './business.actions';
import { BusinessApiService } from '../services/business-api.service';
import { NotificationService } from '../../../core/services/notification.service';

@Injectable()
export class BusinessEffects {
  private readonly actions$ = inject(Actions);
  private readonly api = inject(BusinessApiService);
  private readonly notification = inject(NotificationService);
  private readonly router = inject(Router);

  loadBusinesses$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ActionsList.loadBusinesses),
      switchMap(() =>
        this.api.getMyBusinesses().pipe(
          map(businesses =>
            ActionsList.loadBusinessesSuccess({
              businesses
            })
          ),
          catchError(error =>
            of(
              ActionsList.loadBusinessesFailure({
                error:
                  error.error?.message ??
                  'Unable to load business profiles.'
              })
            )
          )
        )
      )
    )
  );

  createBusiness$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ActionsList.createBusiness),
      switchMap(({ request }) =>
        this.api.create(request).pipe(
          map(business =>
            ActionsList.createBusinessSuccess({
              business
            })
          ),
          catchError(error =>
            of(
              ActionsList.createBusinessFailure({
                error:
                  error.error?.message ??
                  'Unable to create business profile.'
              })
            )
          )
        )
      )
    )
  );

  createSuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(ActionsList.createBusinessSuccess),
        tap(() =>
          this.notification.success(
            'Business profile created successfully.'
          )
        )
      ),
    { dispatch: false }
  );

  updateBusiness$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ActionsList.updateBusiness),
      switchMap(({ id, request }) =>
        this.api.update(id, request).pipe(
          map(business =>
            ActionsList.updateBusinessSuccess({
              business
            })
          ),
          catchError(error =>
            of(
              ActionsList.updateBusinessFailure({
                error:
                  error.error?.message ??
                  'Unable to update business profile.'
              })
            )
          )
        )
      )
    )
  );

  updateSuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(ActionsList.updateBusinessSuccess),
        tap(() =>
          this.notification.success(
            'Business profile updated successfully.'
          )
        )
      ),
    { dispatch: false }
  );

  loadAllBusinesses$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ActionsList.loadAllBusinesses),
      switchMap(() =>
        this.api.getAll().pipe(
          map(businesses =>
            ActionsList.loadAllBusinessesSuccess({
              businesses
            })
          ),
          catchError(error =>
            of(
              ActionsList.loadAllBusinessesFailure({
                error:
                  error.error?.message ??
                  'Unable to load businesses.'
              })
            )
          )
        )
      )
    )
  );
}

