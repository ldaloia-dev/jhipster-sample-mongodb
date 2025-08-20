import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IDummy } from '../dummy.model';

@Component({
  selector: 'jhi-dummy-detail',
  templateUrl: './dummy-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class DummyDetailComponent {
  dummy = input<IDummy | null>(null);

  previousState(): void {
    window.history.back();
  }
}
