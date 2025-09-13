import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FilterItemPage } from './filter-item.page';

describe('FilterItemPage', () => {
  let component: FilterItemPage;
  let fixture: ComponentFixture<FilterItemPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(FilterItemPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
