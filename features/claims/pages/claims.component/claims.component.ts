import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject
} from '@angular/core';

import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { Store } from '@ngrx/store';

import { TokenService } from '../../../../core/services/token.service';

import {
  Claim,
  ClaimStatus,
  ClaimType,
  Recovery
} from '../../models/claims.models';

import * as ClaimsActions from '../../store/claims.actions';

import {
  selectClaims,
  selectClaimsLoading,
  selectClaimsCreating,
  selectClaimsUpdating,
  selectClaimsError,
  selectClaimsSuccessMessage,
  selectSelectedClaimRecoveries
} from '../../store/claims.selectors';

@Component({
  selector: 'app-claims',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './claims.component.html',
  styleUrl: './claims.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ClaimsComponent implements OnInit {

  private readonly store = inject(Store);
  private readonly fb = inject(FormBuilder);
  private readonly tokenService = inject(TokenService);

  readonly claims$ =
    this.store.select(selectClaims);

  readonly loading$ =
    this.store.select(selectClaimsLoading);

  readonly creating$ =
    this.store.select(selectClaimsCreating);

  readonly updating$ =
    this.store.select(selectClaimsUpdating);

  readonly error$ =
    this.store.select(selectClaimsError);

  readonly successMessage$ =
    this.store.select(selectClaimsSuccessMessage);

  readonly recoveries$ =
    this.store.select(selectSelectedClaimRecoveries);

  activeTab:
    'claims' | 'recoveries' = 'claims';

  selectedClaim: Claim | null = null;

  showCreateClaim = false;
  showStatusModal = false;
  showRecoveryModal = false;

  selectedRecovery: Recovery | null = null;

  readonly claimTypes: ClaimType[] = [
    'PROPERTY_DAMAGE',
    'FIRE',
    'FLOOD',
    'THEFT',
    'LIABILITY',
    'ACCIDENT',
    'OTHER'
  ];

  readonly claimStatuses: ClaimStatus[] = [
    'SUBMITTED',
    'UNDER_REVIEW',
    'APPROVED',
    'REJECTED',
    'SETTLED'
  ];

  readonly claimForm = this.fb.nonNullable.group({
    businessId: [
      0,
      [
        Validators.required,
        Validators.min(1)
      ]
    ],
    policyId: [
      0,
      [
        Validators.required,
        Validators.min(1)
      ]
    ],
    claimNumber: [
      '',
      Validators.required
    ],
    claimType: [
      'PROPERTY_DAMAGE' as ClaimType,
      Validators.required
    ],
    incidentDate: [
      '',
      Validators.required
    ],
    reportedDate: [
      '',
      Validators.required
    ],
    claimedAmount: [
      0,
      [
        Validators.required,
        Validators.min(0.01)
      ]
    ],
    description: ['']
  });

  readonly statusForm = this.fb.nonNullable.group({
    status: [
      'UNDER_REVIEW' as ClaimStatus,
      Validators.required
    ],
    approvedAmount: [
      0
    ],
    assignedAdjusterId: [
      0
    ]
  });

  readonly recoveryForm = this.fb.nonNullable.group({
    recoveryAmount: [
      0,
      [
        Validators.required,
        Validators.min(0.01)
      ]
    ],
    recoverySource: [''],
    description: ['']
  });

  readonly recoveryStatusForm = this.fb.nonNullable.group({
    status: [
      '',
      Validators.required
    ]
  });

  ngOnInit(): void {
    this.loadClaims();
  }

  get role(): string | null {
    const token=
    localStorage.getItem('risk_twin_token');
    if(!token){
      return null;
    }
    try{
      const payload=JSON.parse(
        atob(token.split('.')[1])
      );
      return payload.role??null;
    }
    catch{
      return null;
    }
    // return this.tokenService.getRole();
  }

  get canCreateClaim(): boolean {
    return (
      this.role === 'BUSINESS_OWNER' ||
      this.role === 'ADMIN'
    );
  }

  get canManageClaims(): boolean {
    return (
      this.role === 'CLAIMS_ADJUSTER' ||
      this.role === 'ADMIN'
    );
  }

  get canManageRecovery(): boolean {
    return (
      this.role === 'CLAIMS_ADJUSTER' ||
      this.role === 'ADMIN'
    );
  }

  loadClaims(): void {
    if (this.role === 'BUSINESS_OWNER') {
      this.store.dispatch(
        ClaimsActions.loadOwnerClaims()
      );
    } else {
      this.store.dispatch(
        ClaimsActions.loadClaims()
      );
    }
  }

  openCreateClaim(): void {
    this.claimForm.reset({
      businessId: 0,
      policyId: 0,
      claimNumber: '',
      claimType: 'PROPERTY_DAMAGE',
      incidentDate: '',
      reportedDate: '',
      claimedAmount: 0,
      description: ''
    });

    this.showCreateClaim = true;
  }

  closeCreateClaim(): void {
    this.showCreateClaim = false;
  }

  submitClaim(): void {
    if (this.claimForm.invalid) {
      this.claimForm.markAllAsTouched();
      return;
    }

    this.store.dispatch(
      ClaimsActions.createClaim({
        request: this.claimForm.getRawValue()
      })
    );

    this.showCreateClaim = false;
  }

  viewClaim(claim: Claim): void {
    this.selectedClaim = claim;

    this.store.dispatch(
      ClaimsActions.loadClaimRecoveries({
        claimId: claim.id
      })
    );

    this.activeTab = 'claims';
  }

  closeClaimDetails(): void {
    this.selectedClaim = null;
  }

  openStatusModal(claim: Claim): void {
    this.selectedClaim = claim;

    this.statusForm.reset({
      status: claim.status,
      approvedAmount:
        claim.approvedAmount ?? 0,
      assignedAdjusterId:
        claim.assignedAdjusterId ?? 0
    });

    this.showStatusModal = true;
  }

  closeStatusModal(): void {
    this.showStatusModal = false;
  }

  updateClaimStatus(): void {
    if (!this.selectedClaim) {
      return;
    }

    if (this.statusForm.invalid) {
      this.statusForm.markAllAsTouched();
      return;
    }

    const value =
      this.statusForm.getRawValue();

    const request = {
      status: value.status,
      approvedAmount:
        value.approvedAmount > 0
          ? value.approvedAmount
          : undefined,
      assignedAdjusterId:
        value.assignedAdjusterId > 0
          ? value.assignedAdjusterId
          : undefined
    };

    this.store.dispatch(
      ClaimsActions.updateClaimStatus({
        claimId: this.selectedClaim.id,
        request
      })
    );

    this.showStatusModal = false;
  }

  openRecoveryModal(claim: Claim): void {
    this.selectedClaim = claim;

    this.recoveryForm.reset({
      recoveryAmount: 0,
      recoverySource: '',
      description: ''
    });

    this.showRecoveryModal = true;
  }

  closeRecoveryModal(): void {
    this.showRecoveryModal = false;
  }

  createRecovery(): void {
    if (!this.selectedClaim) {
      return;
    }

    if (this.recoveryForm.invalid) {
      this.recoveryForm.markAllAsTouched();
      return;
    }

    this.store.dispatch(
      ClaimsActions.createRecovery({
        claimId: this.selectedClaim.id,
        request:
          this.recoveryForm.getRawValue()
      })
    );

    this.showRecoveryModal = false;
  }

  selectRecoveries(): void {
    if (!this.selectedClaim) {
      return;
    }

    this.activeTab = 'recoveries';

    this.store.dispatch(
      ClaimsActions.loadClaimRecoveries({
        claimId: this.selectedClaim.id
      })
    );
  }

  openRecoveryStatus(
    recovery: Recovery
  ): void {
    this.selectedRecovery = recovery;

    this.recoveryStatusForm.reset({
      status: recovery.status
    });
  }

  updateRecoveryStatus(): void {
    if (!this.selectedRecovery) {
      return;
    }

    if (this.recoveryStatusForm.invalid) {
      this.recoveryStatusForm.markAllAsTouched();
      return;
    }

    this.store.dispatch(
      ClaimsActions.updateRecoveryStatus({
        recoveryId:
          this.selectedRecovery.id,
        request:
          this.recoveryStatusForm.getRawValue()
      })
    );

    this.selectedRecovery = null;
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'APPROVED':
      case 'SETTLED':
        return 'status-success';

      case 'REJECTED':
        return 'status-danger';

      case 'UNDER_REVIEW':
        return 'status-warning';

      default:
        return 'status-neutral';
    }
  }

  formatCurrency(
    value: number | null
  ): string {
    return new Intl.NumberFormat(
      'en-IN',
      {
        style: 'currency',
        currency: 'INR',
        maximumFractionDigits: 2
      }
    ).format(value ?? 0);
  }
  countStatus(
  claims: Claim[],
  status: ClaimStatus
): number {
  return claims.filter(
    claim => claim.status === status
  ).length;
}

}

