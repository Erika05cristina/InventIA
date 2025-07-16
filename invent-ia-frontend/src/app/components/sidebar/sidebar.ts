import { Component, ElementRef, ViewChild, AfterViewInit, HostListener,signal } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  imports: [RouterModule],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.scss']
})

export class Sidebar implements AfterViewInit {
  @ViewChild('sidebar') sidebarRef!: ElementRef<HTMLElement>;
  @ViewChild('menuIcon') menuIconRef!: ElementRef<HTMLElement>;

  private readonly collapsedSidebarHeight = '56px';
  private readonly fullSidebarHeight = 'calc(100vh - 32px)';
  readonly isMenuActive = signal(false);

  readonly primaryNav = [
    { icon: 'network_intel_node', label: 'Predict', route: '/upload' },
    { icon: 'dashboard', label: 'Dashboard' },
    { icon: 'clarify', label: 'Explanation', route: '/explanation' },
    { icon: 'group', label: 'Agent', route: '/agent' },
    { icon: 'insert_chart', label: 'Analytics' },
    { icon: 'history', label: 'Bookmarks' },
    { icon: 'settings', label: 'Settings' },
  ];

  readonly secondaryNav = [
    { icon: 'account_circle', label: 'Profile' },
    { icon: 'logout', label: 'Logout', route: '/login'},
  ];

  ngAfterViewInit(): void {
    this.updateSidebarHeight();
  }

  toggleCollapsed(): void {
    this.sidebarRef.nativeElement.classList.toggle('collapsed');
  }

  toggleMenu(): void {
    this.isMenuActive.update((active) => {
      const next = !active;
      this.adjustSidebar(next);
      return next;
    });
  }

  @HostListener('window:resize')
  updateSidebarHeight(): void {
    const sidebar = this.sidebarRef.nativeElement;
    const isWideScreen = window.innerWidth >= 1024;

    if (isWideScreen) {
      sidebar.style.height = this.fullSidebarHeight;
    } else {
      sidebar.classList.remove('collapsed');
      sidebar.style.height = 'auto';
      this.adjustSidebar(this.isMenuActive());
    }
  }

  private adjustSidebar(active: boolean): void {
    const sidebar = this.sidebarRef.nativeElement;
    const icon = this.menuIconRef.nativeElement;

    sidebar.style.height = active ? `${sidebar.scrollHeight}px` : this.collapsedSidebarHeight;
    icon.innerText = active ? 'close' : 'menu';
  }
}