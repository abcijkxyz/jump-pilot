; Script generated by the HM NIS Edit Script Wizard.

; HM NIS Edit Wizard helper defines
!define PRODUCT_NAME "OpenJUMP"
!define PRODUCT_VERSION "1.2 C"
!define PRODUCT_PUBLISHER "Jump Pilot Project"
!define PRODUCT_WEB_SITE "http://www.openjump.org"
!define PRODUCT_DIR_REGKEY "Software\Microsoft\Windows\CurrentVersion\App Paths\openjump.exe"
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"
  
!define JRE_VERSION "1.5"
!define JRE_URL "http://dlc.sun.com/jdk/jre-1_5_0_01-windows-i586-p.exe"

; MUI 1.67 compatible ------
!include "MUI.nsh"

; MUI Settings
!define MUI_ABORTWARNING
!define MUI_WELCOMEFINISHPAGE_BITMAP side_left.bmp
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install.ico"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall.ico"
  
; Welcome page
!insertmacro MUI_PAGE_WELCOME
; License page
!insertmacro MUI_PAGE_LICENSE "openjump-1-2-Cwin\license.txt"
; Directory page
!insertmacro MUI_PAGE_DIRECTORY
; Instfiles page
!insertmacro MUI_PAGE_INSTFILES
; Finish page
!define MUI_FINISHPAGE_RUN "$INSTDIR\bin\openjump.exe"
!insertmacro MUI_PAGE_FINISH

; Uninstaller pages
!insertmacro MUI_UNPAGE_INSTFILES

; Language files
!insertmacro MUI_LANGUAGE "English"

; MUI end ------

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
OutFile "Setup-openjump12c.exe"
InstallDir "$PROGRAMFILES\OpenJUMP"
InstallDirRegKey HKLM "${PRODUCT_DIR_REGKEY}" ""
ShowInstDetails show
ShowUnInstDetails show

Section "Hauptgruppe" SEC01
  Call DetectJRE
  SetOutPath "$INSTDIR\bin\"
  SetOverwrite ifnewer
  File "openjump-1-2-Cwin\bin\openjump.exe"
  File "openjump-1-2-Cwin\bin\log4j.xml"
  File "openjump-1-2-Cwin\bin\openjump.sh"
  File "openjump-1-2-Cwin\bin\openjump.bat"
  CreateDirectory "$SMPROGRAMS\OpenJUMP"
  CreateShortCut "$SMPROGRAMS\OpenJUMP\OpenJUMP.lnk" "$INSTDIR\bin\openjump.exe"
  CreateShortCut "$DESKTOP\OpenJUMP.lnk" "$INSTDIR\bin\openjump.exe"
  SetOutPath "$INSTDIR"
  File "openjump-1-2-Cwin\readme.txt"
  File "openjump-1-2-Cwin\license.txt"
  File "openjump-1-2-Cwin\apache.txt"
  File "openjump-1-2-Cwin\Using_MrSIDPlugIn.txt"
  File "openjump-1-2-Cwin\workbench-properties.xml"
  SetOutPath "$INSTDIR\lib\"
  File "openjump-1-2-Cwin\lib\jump-api-20070811-0017.jar"
  File "openjump-1-2-Cwin\lib\jai_core.jar"
  File "openjump-1-2-Cwin\lib\jump-workbench-20070811-0017.jar"
  File "openjump-1-2-Cwin\lib\log4j-1.2.8.jar"
  File "openjump-1-2-Cwin\lib\xercesImpl.jar"
  File "openjump-1-2-Cwin\lib\batik-all.jar"
  File "openjump-1-2-Cwin\lib\jai_codec.jar"
  File "openjump-1-2-Cwin\lib\bsh-2.0b4.jar"
  File "openjump-1-2-Cwin\lib\Buoy.jar"
  File "openjump-1-2-Cwin\lib\ermapper.jar"
  File "openjump-1-2-Cwin\lib\Jama-1.0.1.jar"
  File "openjump-1-2-Cwin\lib\jdom.jar"
  File "openjump-1-2-Cwin\lib\jmat_5.0m.jar"
  File "openjump-1-2-Cwin\lib\jts-1.7.2.jar"
  File "openjump-1-2-Cwin\lib\postgis_1_0_0.jar"
  File "openjump-1-2-Cwin\lib\postgresql-8.1dev-403.jdbc2.jar"
  File "openjump-1-2-Cwin\lib\xml-apis.jar"
  File "openjump-1-2-Cwin\lib\xml-apis-ext.jar"
  SetOutPath "$INSTDIR\lib\ext\"
  File "openjump-1-2-Cwin\lib\ext\readme.txt"
  SetOutPath "$INSTDIR\lib\ext\BeanTools\"
  File "openjump-1-2-Cwin\lib\ext\BeanTools\0-Help.bsh"
  File "openjump-1-2-Cwin\lib\ext\BeanTools\5-ChangeAttributeValue.bsh"
  File "openjump-1-2-Cwin\lib\ext\BeanTools\1-HelloWorld.bsh"
  File "openjump-1-2-Cwin\lib\ext\BeanTools\2-NewLayer.bsh"
  File "openjump-1-2-Cwin\lib\ext\BeanTools\3-Populate.bsh"
  File "openjump-1-2-Cwin\lib\ext\BeanTools\4-AddAttribute.bsh"
  File "openjump-1-2-Cwin\lib\ext\BeanTools\dissolve.bsh"
  File "openjump-1-2-Cwin\lib\ext\BeanTools\RefreshScriptsMenu.bsh"
SectionEnd

Section -AdditionalIcons
  SetOutPath $INSTDIR
  WriteIniStr "$INSTDIR\${PRODUCT_NAME}.url" "InternetShortcut" "URL" "${PRODUCT_WEB_SITE}"
  CreateShortCut "$SMPROGRAMS\OpenJUMP\Website.lnk" "$INSTDIR\${PRODUCT_NAME}.url"
  CreateShortCut "$SMPROGRAMS\OpenJUMP\Uninstall.lnk" "$INSTDIR\uninst.exe"
SectionEnd

Section -Post
  WriteUninstaller "$INSTDIR\uninst.exe"
  WriteRegStr HKLM "${PRODUCT_DIR_REGKEY}" "" "$INSTDIR\bin\openjump.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayIcon" "$INSTDIR\bin\openjump.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "URLInfoAbout" "${PRODUCT_WEB_SITE}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
SectionEnd

Function un.onUninstSuccess
  HideWindow
  MessageBox MB_ICONINFORMATION|MB_OK "OpenJUMP wurde erfolgreich deinstalliert."
FunctionEnd

Function un.onInit
  MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "M�chten Sie OpenJUMP und alle seinen Komponenten deinstallieren?" IDYES +2
  Abort
FunctionEnd

Function GetJRE
        MessageBox MB_OKCANCEL|MB_ICONEXCLAMATION "${PRODUCT_NAME} uses Java ${JRE_VERSION}, it will now \
                         be downloaded and installed"  IDCANCEL NoDownloadJava
        
        StrCpy $2 "$TEMP\Java Runtime Environment.exe"
        nsisdl::download /TIMEOUT=30000 ${JRE_URL} $2
        Pop $R0 ;Get the return value
                StrCmp $R0 "success" +3
                MessageBox MB_OK "Download failed: $R0"
                Quit
        ExecWait $2
        Delete $2
        NoDownloadJava:
                Quit
FunctionEnd


Function DetectJRE
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" \
             "CurrentVersion"
  StrCmp $2 ${JRE_VERSION} done

  Call GetJRE

  done:
FunctionEnd

Section Uninstall
  Delete "$INSTDIR\${PRODUCT_NAME}.url"
  Delete "$INSTDIR\uninst.exe"
  Delete "$INSTDIR\lib\*.jar"
  Delete "$INSTDIR\lib\ext\BeanTools\*.bsh"
  Delete "$INSTDIR\lib\ext\readme.txt"
  Delete "$INSTDIR\*.*"
  Delete "$INSTDIR\bin\*.*"

  Delete "$SMPROGRAMS\OpenJUMP\Uninstall.lnk"
  Delete "$SMPROGRAMS\OpenJUMP\Website.lnk"
  Delete "$DESKTOP\OpenJUMP.lnk"
  Delete "$SMPROGRAMS\OpenJUMP\OpenJUMP.lnk"

  RMDir "$SMPROGRAMS\OpenJUMP"
  RMDir "$INSTDIR\lib\ext\BeanTools\"
  RMDir "$INSTDIR\lib\ext\"
  RMDir "$INSTDIR\lib\"
  RMDir "$INSTDIR\bin\"
  RMDir "$INSTDIR"

  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
  DeleteRegKey HKLM "${PRODUCT_DIR_REGKEY}"
  SetAutoClose true
SectionEnd