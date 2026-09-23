import { Injectable, signal } from '@angular/core';

export interface Notification {
  type: 'success' | 'error' | 'info';
  message: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  readonly current = signal<Notification | null>(null);

  show(
    message: string,
    type: Notification['type'] = 'info'
  ): void {
    this.current.set({ message, type });

    setTimeout(() => {
      this.current.set(null);
    }, 3500);
  }

  success(message: string): void {
    this.show(message, 'success');
  }

  error(message: string): void {
    this.show(message, 'error');
  }
}
