import { Component, OnInit } from '@angular/core';
import { MapService } from '../../services/map.service';
import { CleanerService } from '../../services/cleaner.service';
import { TelemetryService } from '../../services/telemetry.service';

@Component({
  selector: 'app-map-screen',
  templateUrl: './map-screen.component.html',
  styleUrls: ['./map-screen.component.less']
})
export class MapScreenComponent implements OnInit {

  currentMap: String[][] = [
    ["#", " ", "#"],
    ["#", " ", "#"]
  ];

  visited: any[] = [];

  floorArea: number = 6;
  cleaningArea: number = 2;
  cleanedTiles: number = 0;
  cleaningSpeed: number = 0;

  firstMoveMessageTimestamp: number = null;

  currentCleaner: any = {
  };

  Math: any = Math;

  constructor(private mapService: MapService, private cleanerService: CleanerService, private telemetryService: TelemetryService) { }

  ngOnInit() {
    this.mapService.getMap().subscribe((data: String[][]) => {
      this.visited = [];
      
      this.floorArea = 0;
      this.cleaningArea = 0;

      for (var y = 0; y < data.length; y++) {
        this.visited.push([]);
        
        for (var x = 0; x < data[y].length; x++) {
          this.visited[y].push(0);
          this.floorArea++;

          if (data[y][x] != "#") {
            this.cleaningArea++;
          }
        }
      }

      this.currentMap = data;
    });
    this.cleanerService.getCleaner().subscribe((data: any) => {
      this.currentCleaner = data;
    });
    this.telemetryService.start().subscribe(this.onTelemetryMessage.bind(this));
  }

  public onTelemetryMessage(message): void {

    if (this.firstMoveMessageTimestamp == null) {
      this.firstMoveMessageTimestamp = Date.now();
    }

    let x = message.x;
    let y = message.y;
    let currentTimeMs = Date.now();
    let runtimeMs = currentTimeMs - this.firstMoveMessageTimestamp;

    if (this.visited[y][x] == 0) {
      this.cleanedTiles++;
    }

    // Recalculate how quickly tiles are getting cleaned.
    this.cleaningSpeed = this.cleanedTiles / (runtimeMs / 1000);

    if (this.visited[y][x] == 0 || x != this.currentCleaner.positionX || y != this.currentCleaner.positionY) {
      this.visited[y][x]++;
    }
    this.currentCleaner.positionX = message.x;
    this.currentCleaner.positionY = message.y;
    this.currentCleaner.direction = message.direction;
  }

  startCleaningClick() {
    // Reset the stats.
    this.cleanedTiles = 0;
    this.cleaningSpeed = 0;
    this.firstMoveMessageTimestamp = null;

    for (let y = 0; y < this.visited.length; y++) {
      for (let x = 0; x < this.visited[y].length; x++) {
        this.visited[y][x] = false;
      }
    }

    this.cleanerService.startCleaning(this.currentCleaner).subscribe(() => {
      // Angular HTTP requires a dummy subscriber to kick off the request.
    });
  }
}