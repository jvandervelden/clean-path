import { Component, OnInit } from '@angular/core';
import { MapService } from '../../services/map.service';
import { CleanerService } from '../../services/cleaner.service';
import { TelematryService } from '../../services/telematry.service';

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

  visited: boolean[][] = [];

  floorArea: number = 6;
  cleaningArea: number = 2;
  cleanedTiles: number = 0;
  averageSpeed: number = 0;
  cleaningSpeed: number = 0;

  firstMoveMessageTimestamp: number = null;
  lastMoveMessageTimestamp: number = null;

  currentCleaner: any = {
  };

  Math: any = Math;

  constructor(private mapService: MapService, private cleanerService: CleanerService, private telematryService: TelematryService) { }

  ngOnInit() {
    this.mapService.getMap().subscribe((data: String[][]) => {
      this.visited = [];
      
      this.floorArea = 0;
      this.cleaningArea = 0;

      for (var y = 0; y < data.length; y++) {
        this.visited.push([]);
        
        for (var x = 0; x < data[y].length; x++) {
          this.visited[y].push(false);
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
    this.telematryService.start().subscribe(this.onTelematryMessage.bind(this));
  }

  public isVisited(x: number, y: number): boolean {
    if (y < this.visited.length && x < this.visited[y].length) {
      return this.visited[y][x];
    }

    return false;
  }

  public onTelematryMessage(message): void {

    if (this.firstMoveMessageTimestamp == null) {
      this.firstMoveMessageTimestamp = Date.now();
    }

    if (this.lastMoveMessageTimestamp == null) {
      this.lastMoveMessageTimestamp = Date.now();
    }

    let x = message.x;
    let y = message.y;
    let currentTimeMs = Date.now();
    let secondsSinceLastMove = (currentTimeMs - this.lastMoveMessageTimestamp) / 1000;
    let runtimeMs = currentTimeMs - this.firstMoveMessageTimestamp;

    // Calculate a moving average of the cleaner's speed
    if (x != this.currentCleaner.positionX && y != this.currentCleaner.positionY) {
      this.averageSpeed = (this.averageSpeed + 1 / secondsSinceLastMove) / 2;
      this.lastMoveMessageTimestamp = currentTimeMs;
    }

    if (!this.visited[y][x]) {
      this.cleanedTiles++;
    }

    // Recalculate how quickly tiles are getting cleaned.
    this.cleaningSpeed = this.cleanedTiles / (runtimeMs / 1000);

    this.visited[y][x] = true;
    this.currentCleaner.positionX = message.x;
    this.currentCleaner.positionY = message.y;
    this.currentCleaner.direction = message.direction;
  }

  
  startCleaningClick() {
    // Reset the stats.
    this.cleanedTiles = 0;
    this.averageSpeed = 0;
    this.cleaningSpeed = 0;
    this.firstMoveMessageTimestamp = null;
    this.lastMoveMessageTimestamp = null;

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