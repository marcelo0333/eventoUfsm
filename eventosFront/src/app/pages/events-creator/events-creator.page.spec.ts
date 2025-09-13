import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EventsCreatorPage } from './events-creator.page';

describe('EventsCreatorPage', () => {
  let component: EventsCreatorPage;
  let fixture: ComponentFixture<EventsCreatorPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(EventsCreatorPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
