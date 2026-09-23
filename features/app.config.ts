import { ApplicationConfig } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { provideStore, provideState } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { policyReducer } from './features/policies/store/policy.reducer';
import { PolicyEffects } from './features/policies/store/policy.effects';


import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { errorInterceptor } from './core/interceptors/error.interceptor';
import { authReducer } from './features/auth/store/auth.reducer';
import { AuthEffects } from './features/auth/store/auth.effects';

import { businessReducer } from './features/business/store/business.reducer';
import { BusinessEffects } from './features/business/store/business.effects';
import { riskReducer } from './features/risk/store/risk.reducer';
import { RiskEffects } from './features/risk/store/risk.effects';
import { ClaimsEffects } from './features/claims/store/claims.effects';
import { claimsReducer } from './features/claims/store/claims.reducer';
import { NotificationEffects } from './features/notifications/store/notification.effects';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([
        authInterceptor,
        errorInterceptor
      ])
    ),
    provideStore(),
    provideState('auth', authReducer),
     provideState('business', businessReducer),
     provideState('policies', policyReducer),
     provideState('risk', riskReducer),
     provideState('claims', claimsReducer),
     

    provideEffects(AuthEffects,BusinessEffects, PolicyEffects, RiskEffects,   ClaimsEffects,   NotificationEffects),

  ]
};


