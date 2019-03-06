import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-map-viewer',
  templateUrl: './map-viewer.component.html',
  styleUrls: ['./map-viewer.component.less']
})
export class MapViewerComponent implements OnInit {

  @Input()
  map: String[][] = [[]];

  @Input()
  visited: boolean[][] = [[]];

  @Input()
  cleaner: any = {};

  constructor() { }

  ngOnInit() {
  }
}
