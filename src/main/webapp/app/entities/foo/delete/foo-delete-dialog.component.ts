import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IFoo } from '../foo.model';
import { FooService } from '../service/foo.service';

@Component({
  templateUrl: './foo-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class FooDeleteDialogComponent {
  foo?: IFoo;

  protected fooService = inject(FooService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.fooService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
