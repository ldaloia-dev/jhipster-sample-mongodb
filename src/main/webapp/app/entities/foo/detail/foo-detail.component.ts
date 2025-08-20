import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IFoo } from '../foo.model';

@Component({
  selector: 'jhi-foo-detail',
  templateUrl: './foo-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class FooDetailComponent {
  foo = input<IFoo | null>(null);

  previousState(): void {
    window.history.back();
  }
}
