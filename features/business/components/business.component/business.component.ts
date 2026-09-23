import { Component, inject } from '@angular/core';
import {
  AsyncPipe,
  CurrencyPipe,
  DatePipe
} from '@angular/common';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { Store } from '@ngrx/store';

import * as BusinessActions
  from '../../store/business.actions';

import {
  selectBusinesses,
  selectBusinessError,
  selectLoading,
  selectSaving
} from '../../store/business.selector';
import { PageHeaderComponent } from '../../../../shared/components/page-header.component/page-header.component';


@Component({
  selector: 'app-business',
  standalone: true,
  imports: [
    AsyncPipe,
    CurrencyPipe,
    DatePipe,
    ReactiveFormsModule,
    PageHeaderComponent
  ],
  templateUrl: './business.component.html',
  styleUrl: './business.component.css'
})
export class BusinessComponent {
  private readonly store = inject(Store);
  private readonly fb = inject(FormBuilder);

  readonly businesses$ =
    this.store.select(selectBusinesses);

  readonly loading$ =
    this.store.select(selectLoading);

  readonly saving$ =
    this.store.select(selectSaving);

  readonly error$ =
    this.store.select(selectBusinessError);

  editingId: number | null = null;
  showForm = false;

  readonly form = this.fb.nonNullable.group({
    businessName: ['', Validators.required],
    registrationNumber: ['', Validators.required],
    businessType: ['', Validators.required],
    industry: ['', Validators.required],
    address: ['', Validators.required],
    city: ['', Validators.required],
    state: ['', Validators.required],
    postalCode: ['', Validators.required],
    country: ['India', Validators.required],
    contactEmail: ['', [
      Validators.required,
      Validators.email
    ]],
    contactPhone: ['', Validators.required],
    annualRevenue: [0, [
      Validators.required,
      Validators.min(0)
    ]],
    employeeCount: [1, [
      Validators.required,
      Validators.min(1)
    ]],
    establishedDate: ['', Validators.required]
  });

  constructor() {
    this.load();
  }

  load(): void {
    this.store.dispatch(
      BusinessActions.loadBusinesses()
    );
  }

  openCreate(): void {
    this.editingId = null;
    this.form.reset({
      businessName: '',
      registrationNumber: '',
      businessType: '',
      industry: '',
      address: '',
      city: '',
      state: '',
      postalCode: '',
      country: 'India',
      contactEmail: '',
      contactPhone: '',
      annualRevenue: 0,
      employeeCount: 1,
      establishedDate: ''
    });
    this.showForm = true;
  }

  edit(id: number): void {
    const business = this.getBusiness(id);

    if (!business) return;

    this.editingId = id;

    this.form.patchValue({
      businessName: business.businessName,
      registrationNumber: business.registrationNumber,
      businessType: business.businessType,
      industry: business.industry,
      address: business.address,
      city: business.city,
      state: business.state,
      postalCode: business.postalCode,
      country: business.country,
      contactEmail: business.contactEmail,
      contactPhone: business.contactPhone,
      annualRevenue: business.annualRevenue,
      employeeCount: business.employeeCount,
      establishedDate: business.establishedDate
    });

    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.editingId = null;
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const request = this.form.getRawValue();

    if (this.editingId) {
      this.store.dispatch(
        BusinessActions.updateBusiness({
          id: this.editingId,
          request
        })
      );
    } else {
      this.store.dispatch(
        BusinessActions.createBusiness({
          request
        })
      );
    }

    this.showForm = false;
  }

  getBusiness(id: number) {
    let result: any;

    this.store.select(selectBusinesses)
      .subscribe(items => {
        result = items.find(item => item.id === id);
      })
      .unsubscribe();

    return result;
  }
}

