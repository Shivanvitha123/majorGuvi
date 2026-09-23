import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class TokenService {
  private readonly key = 'risk_twin_token';

  set(token: string): void {
    localStorage.setItem(this.key, token);
  }

  get(): string | null {
    return localStorage.getItem(this.key);
  }

  clear(): void {
    localStorage.removeItem(this.key);
  }

  hasToken(): boolean {
    return !!this.get();
  }

  payload(): Record<string, any> | null {
    try {
      const token = this.get();
      if (!token) return null;

      const part = token.split('.')[1];
      return JSON.parse(
        atob(part.replace(/-/g, '+').replace(/_/g, '/'))
      );
    } catch {
      return null;
    }
  }
}
