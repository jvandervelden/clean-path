import { TestBed } from '@angular/core/testing';

import { TelematryService } from './telematry.service';

describe('TelematryService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TelematryService = TestBed.get(TelematryService);
    expect(service).toBeTruthy();
  });
});
