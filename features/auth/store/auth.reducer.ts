import { createReducer, on } from '@ngrx/store';
import * as Auth from './auth.actions';
import { User } from '../../../core/models/user.model';

export interface AuthState {
  user: User | null;
  loading: boolean;
  error: string | null;
}

export const initialState: AuthState = {
  user: null,
  loading: false,
  error: null
};

export const authReducer = createReducer(
  initialState,

  on(
    Auth.login,
    Auth.register,
    Auth.loadCurrentUser,
    state => ({
      ...state,
      loading: true,
      error: null
    })
  ),

  on(
    Auth.loginSuccess,
    Auth.registerSuccess,
    Auth.currentUserSuccess,
    (state, { user }) => ({
      ...state,
      user,
      loading: false,
      error: null
    })
  ),

  on(
    Auth.loginFailure,
    Auth.registerFailure,
    (state, { error }) => ({
      ...state,
      loading: false,
      error
    })
  ),

  on(
    Auth.logout,
    () => initialState
  )
);


