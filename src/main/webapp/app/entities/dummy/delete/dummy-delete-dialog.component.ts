import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IDummy } from '../dummy.model';
import { DummyService } from '../service/dummy.service';

@Component({
  templateUrl: './dummy-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class DummyDeleteDialogComponent {
  dummy?: IDummy;

  protected dummyService = inject(DummyService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.dummyService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
